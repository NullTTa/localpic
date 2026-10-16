package com.localpick.domain.post;

import com.localpick.common.BusinessException;
import com.localpick.common.PageResponse;
import com.localpick.config.FileStore;
import com.localpick.domain.comment.CommentRepository;
import com.localpick.domain.hashtag.HashtagService;
import com.localpick.domain.hashtag.PostHashtagRepository;
import com.localpick.domain.insight.PostViewLog;
import com.localpick.domain.insight.PostViewLogRepository;
import com.localpick.domain.like.PostLikeRepository;
import com.localpick.domain.member.Member;
import com.localpick.domain.member.MemberRepository;
import com.localpick.domain.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostLikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final NotificationRepository notificationRepository;
    private final PostViewLogRepository viewLogRepository;
    private final HashtagService hashtagService;
    private final FileStore fileStore;

    private static Pageable latest(int page, int size) {
        return PageRequest.of(page, Math.min(size, 30), Sort.by(Sort.Direction.DESC, "id"));
    }

    /** 전체 피드 (type 이 있으면 홍보/이벤트/일상만) */
    public PageResponse<PostDto.Response> feed(PostType type, int page, int size, Long loginId) {
        Slice<Post> slice = type == null
                ? postRepository.findSliceBy(latest(page, size))
                : postRepository.findByPostType(type, latest(page, size));
        return toPage(slice, loginId);
    }

    /** 내가 팔로우한 계정 + 내 글 */
    public PageResponse<PostDto.Response> followingFeed(Long loginId, int page, int size) {
        return toPage(postRepository.findFollowingFeed(loginId, latest(page, size)), loginId);
    }

    public PageResponse<PostDto.Response> byWriter(Long writerId, int page, int size, Long loginId) {
        return toPage(postRepository.findByWriterId(writerId, latest(page, size)), loginId);
    }

    public PageResponse<PostDto.Response> byHashtag(String tag, int page, int size, Long loginId) {
        return toPage(postRepository.findByHashtag(tag.toLowerCase(), latest(page, size)), loginId);
    }

    public PageResponse<PostDto.Response> byKeyword(String keyword, int page, int size, Long loginId) {
        return toPage(postRepository.findByContentContaining(keyword, latest(page, size)), loginId);
    }

    public List<PostDto.Response> ongoingEvents(Long loginId) {
        List<Post> posts = postRepository.findOngoingEvents(LocalDate.now(), PageRequest.of(0, 5));
        return toResponses(posts, loginId);
    }

    /** 상세 조회: 조회수 +1, 조회 로그 저장 (작성자 본인이 볼 때는 세지 않음) */
    @Transactional
    public PostDto.Response detail(Long postId, Long loginId) {
        Post post = find(postId);
        if (!post.isWrittenBy(loginId == null ? -1L : loginId)) {
            viewLogRepository.save(new PostViewLog(postId, loginId, LocalDateTime.now()));
            postRepository.increaseViewCount(postId);
            post = find(postId);
        }
        return toResponses(List.of(post), loginId).get(0);
    }

    @Transactional
    public PostDto.Response create(Long loginId, PostDto.WriteRequest req) {
        Member writer = memberRepository.findById(loginId).orElseThrow(() -> BusinessException.notFound("회원"));
        validate(writer, req);

        Post post = postRepository.save(Post.builder()
                .writer(writer)
                .content(req.getContent())
                .imageUrl(fileStore.store(req.getImage()))
                .postType(req.getPostType())
                .eventStartDate(req.getEventStartDate())
                .eventEndDate(req.getEventEndDate())
                .linkUrl(req.getLinkUrl())
                .build());
        hashtagService.syncTags(post);
        return toResponses(List.of(post), loginId).get(0);
    }

    @Transactional
    public PostDto.Response update(Long postId, Long loginId, PostDto.WriteRequest req) {
        Post post = find(postId);
        if (!post.isWrittenBy(loginId)) throw BusinessException.forbidden();
        validate(post.getWriter(), req);

        post.update(req.getContent(), req.getPostType(), req.getEventStartDate(), req.getEventEndDate(),
                req.getLinkUrl(), fileStore.store(req.getImage()));
        hashtagService.syncTags(post);
        return toResponses(List.of(post), loginId).get(0);
    }

    @Transactional
    public void delete(Long postId, Long loginId) {
        Post post = find(postId);
        if (!post.isWrittenBy(loginId)) throw BusinessException.forbidden();
        // 자식 데이터부터 지우고 게시물 삭제 (FK 제약)
        likeRepository.deleteByPostId(postId);
        commentRepository.deleteByPostId(postId);
        postHashtagRepository.deleteByPostId(postId);
        notificationRepository.deleteByPostId(postId);
        viewLogRepository.deleteByPostId(postId);
        postRepository.delete(post);
    }

    /** 홍보·이벤트 글은 사업자 계정만, 이벤트는 기간 필수 */
    private void validate(Member writer, PostDto.WriteRequest req) {
        PostType type = req.getPostType() == null ? PostType.DAILY : req.getPostType();
        if (type != PostType.DAILY && !writer.isBusiness()) {
            throw BusinessException.badRequest("홍보·이벤트 게시물은 사업자 계정만 작성할 수 있습니다.");
        }
        if (type == PostType.EVENT) {
            if (req.getEventStartDate() == null || req.getEventEndDate() == null) {
                throw BusinessException.badRequest("이벤트 기간을 입력해주세요.");
            }
            if (req.getEventEndDate().isBefore(req.getEventStartDate())) {
                throw BusinessException.badRequest("이벤트 종료일이 시작일보다 빠릅니다.");
            }
        } else {
            req.setEventStartDate(null);
            req.setEventEndDate(null);
        }
    }

    private Post find(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> BusinessException.notFound("게시물"));
    }

    private PageResponse<PostDto.Response> toPage(Slice<Post> slice, Long loginId) {
        return PageResponse.of(slice, toResponses(slice.getContent(), loginId));
    }

    /**
     * 게시물 목록 -> 응답 DTO.
     * 좋아요 여부와 해시태그를 게시물마다 따로 조회하면 N+1 이 되므로
     * id 목록으로 한 번씩만 조회해서 붙인다. (게시물 20개여도 쿼리 2번)
     */
    private List<PostDto.Response> toResponses(List<Post> posts, Long loginId) {
        List<Long> ids = posts.stream().map(Post::getId).toList();
        Set<Long> likedIds = (loginId == null || ids.isEmpty())
                ? Set.of()
                : new HashSet<>(likeRepository.findLikedPostIds(loginId, ids));
        Map<Long, List<String>> tags = hashtagService.tagsOf(ids);

        return posts.stream()
                .map(p -> new PostDto.Response(p, likedIds.contains(p.getId()), loginId,
                        tags.getOrDefault(p.getId(), List.of())))
                .toList();
    }
}
