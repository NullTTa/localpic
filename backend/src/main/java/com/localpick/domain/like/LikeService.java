package com.localpick.domain.like;

import com.localpick.common.BusinessException;
import com.localpick.domain.member.Member;
import com.localpick.domain.member.MemberRepository;
import com.localpick.domain.notification.NotificationService;
import com.localpick.domain.notification.NotificationType;
import com.localpick.domain.post.Post;
import com.localpick.domain.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final PostLikeRepository likeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;

    /**
     * 좋아요 토글.
     * 1) 이미 눌렀으면 삭제 + 카운트 -1
     * 2) 안 눌렀으면 저장 + 카운트 +1 + 작성자에게 알림
     * (post_id, member_id) 유니크 제약으로 중복 좋아요는 DB 에서도 막힌다.
     */
    @Transactional
    public Map<String, Object> toggle(Long postId, Long loginId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> BusinessException.notFound("게시물"));
        Optional<PostLike> existing = likeRepository.findByPostIdAndMemberId(postId, loginId);

        boolean liked;
        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            postRepository.addLikeCount(postId, -1);
            liked = false;
        } else {
            Member me = memberRepository.getReferenceById(loginId);
            likeRepository.save(new PostLike(post, me));
            postRepository.addLikeCount(postId, 1);
            notificationService.send(post.getWriter().getId(), loginId, NotificationType.LIKE, postId);
            liked = true;
        }
        long likeCount = postRepository.findById(postId).map(Post::getLikeCount).orElse(0L);
        return Map.<String, Object>of("liked", liked, "likeCount", likeCount);
    }
}
