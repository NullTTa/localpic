package com.localpick.domain.insight;

import com.localpick.common.BusinessException;
import com.localpick.domain.follow.FollowRepository;
import com.localpick.domain.hashtag.PostHashtagRepository;
import com.localpick.domain.member.Member;
import com.localpick.domain.member.MemberRepository;
import com.localpick.domain.post.Post;
import com.localpick.domain.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 사업자 계정용 홍보 성과 대시보드.
 * MVC 버전에서는 "올린 홍보 글이 효과가 있었는지" 알 방법이 없었던 한계를 보완한 확장 기능.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InsightService {

    private static final int DAYS = 7;

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    private final PostViewLogRepository viewLogRepository;
    private final PostHashtagRepository postHashtagRepository;

    public Map<String, Object> myInsight(Long loginId) {
        Member me = memberRepository.findById(loginId).orElseThrow(() -> BusinessException.notFound("회원"));
        if (!me.isBusiness()) {
            throw new BusinessException(org.springframework.http.HttpStatus.FORBIDDEN,
                    "인사이트는 사업자 계정만 사용할 수 있습니다.");
        }

        List<Post> posts = postRepository.findByWriterIdOrderByIdDesc(loginId);
        long totalViews = posts.stream().mapToLong(Post::getViewCount).sum();
        long totalLikes = posts.stream().mapToLong(Post::getLikeCount).sum();
        long totalComments = posts.stream().mapToLong(Post::getCommentCount).sum();
        // 참여율 = (좋아요 + 댓글) / 조회수
        double engagementRate = totalViews == 0 ? 0 : (totalLikes + totalComments) * 100.0 / totalViews;

        LocalDate today = LocalDate.now();
        LocalDateTime since = today.minusDays(DAYS - 1).atStartOfDay();

        // 최근 7일 일자별 조회수 / 신규 팔로워 (없는 날도 0 으로 채운다)
        List<Long> postIds = posts.stream().map(Post::getId).toList();
        Map<LocalDate, Long> viewsByDay = postIds.isEmpty() ? Map.<LocalDate, Long>of()
                : viewLogRepository.findByPostIdInAndViewedAtAfter(postIds, since).stream()
                .collect(Collectors.groupingBy(v -> v.getViewedAt().toLocalDate(), Collectors.counting()));
        Map<LocalDate, Long> followersByDay = followRepository
                .findByFollowingIdAndCreatedAtAfter(loginId, since).stream()
                .collect(Collectors.groupingBy(f -> f.getCreatedAt().toLocalDate(), Collectors.counting()));

        List<Map<String, Object>> daily = new ArrayList<>();
        for (int i = DAYS - 1; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            daily.add(Map.<String, Object>of(
                    "date", d.toString(),
                    "views", viewsByDay.getOrDefault(d, 0L),
                    "newFollowers", followersByDay.getOrDefault(d, 0L)));
        }

        // 반응 좋은 게시물 TOP 3 (좋아요 + 댓글*2 기준)
        List<Map<String, Object>> topPosts = posts.stream()
                .sorted(Comparator.comparingLong((Post p) -> p.getLikeCount() + p.getCommentCount() * 2).reversed())
                .limit(3)
                .map(p -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", p.getId());
                    m.put("content", p.getContent().length() > 40 ? p.getContent().substring(0, 40) + "…" : p.getContent());
                    m.put("imageUrl", p.getImageUrl());
                    m.put("postType", p.getPostType());
                    m.put("viewCount", p.getViewCount());
                    m.put("likeCount", p.getLikeCount());
                    m.put("commentCount", p.getCommentCount());
                    return m;
                })
                .toList();

        // 게시물 유형별 평균 좋아요 — 어떤 유형의 글이 반응이 좋은지 비교
        Map<String, Double> avgLikeByType = posts.stream()
                .collect(Collectors.groupingBy(p -> p.getPostType().name(), TreeMap::new,
                        Collectors.averagingLong(Post::getLikeCount)));

        List<Map<String, Object>> topTags = postHashtagRepository.findTopByWriter(loginId, PageRequest.of(0, 5))
                .stream().map(r -> Map.<String, Object>of("name", r[0], "count", r[1])).toList();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("postCount", posts.size());
        result.put("followerCount", followRepository.countByFollowingId(loginId));
        result.put("totalViews", totalViews);
        result.put("totalLikes", totalLikes);
        result.put("totalComments", totalComments);
        result.put("engagementRate", Math.round(engagementRate * 10) / 10.0);
        result.put("daily", daily);
        result.put("topPosts", topPosts);
        result.put("avgLikeByType", avgLikeByType);
        result.put("topHashtags", topTags);
        return result;
    }
}
