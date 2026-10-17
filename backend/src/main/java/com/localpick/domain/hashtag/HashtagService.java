package com.localpick.domain.hashtag;

import com.localpick.domain.post.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HashtagService {

    /** #부평카페, #신메뉴_출시 처럼 한글·영문·숫자·_ 로 된 태그 */
    private static final Pattern TAG_PATTERN = Pattern.compile("#([0-9A-Za-z가-힣_]{1,30})");

    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;

    /** 본문에서 해시태그를 뽑아낸다 (중복 제거, 등장 순서 유지) */
    public static List<String> extract(String content) {
        Set<String> tags = new LinkedHashSet<>();
        if (content == null) return List.of();
        Matcher m = TAG_PATTERN.matcher(content);
        while (m.find()) {
            tags.add(m.group(1).toLowerCase());
        }
        return new ArrayList<>(tags);
    }

    /** 게시물 저장/수정 시 기존 태그를 지우고 본문 기준으로 다시 연결 */
    @Transactional
    public void syncTags(Post post) {
        postHashtagRepository.deleteByPostId(post.getId());
        for (String name : extract(post.getContent())) {
            Hashtag tag = hashtagRepository.findByName(name)
                    .orElseGet(() -> hashtagRepository.save(new Hashtag(name)));
            postHashtagRepository.save(new PostHashtag(post, tag));
        }
    }

    /** postId -> 태그 목록 */
    public Map<Long, List<String>> tagsOf(Collection<Long> postIds) {
        Map<Long, List<String>> result = new HashMap<>();
        if (postIds.isEmpty()) return result;
        for (Object[] row : postHashtagRepository.findTagNames(postIds)) {
            result.computeIfAbsent((Long) row[0], k -> new ArrayList<>()).add((String) row[1]);
        }
        return result;
    }

    public List<Map<String, Object>> trending(int days, int limit) {
        return postHashtagRepository.findTrending(LocalDateTime.now().minusDays(days), PageRequest.of(0, limit))
                .stream()
                .map(r -> Map.<String, Object>of("name", r[0], "count", r[1]))
                .toList();
    }
}
