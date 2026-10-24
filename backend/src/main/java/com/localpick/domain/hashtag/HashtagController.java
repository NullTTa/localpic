package com.localpick.domain.hashtag;

import com.localpick.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HashtagController {

    private final HashtagService hashtagService;

    /** 최근 7일 인기 해시태그 */
    @GetMapping("/api/hashtags/trending")
    public ApiResponse<List<Map<String, Object>>> trending(@RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.ok(hashtagService.trending(7, limit));
    }
}
