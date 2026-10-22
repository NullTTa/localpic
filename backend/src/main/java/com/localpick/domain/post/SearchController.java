package com.localpick.domain.post;

import com.localpick.common.ApiResponse;
import com.localpick.config.LoginMember;
import com.localpick.domain.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final PostService postService;
    private final MemberService memberService;

    /** q 가 #으로 시작하면 해시태그 검색, 아니면 계정 + 본문 검색 */
    @GetMapping("/api/search")
    public ApiResponse<Map<String, Object>> search(@RequestParam String q,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @LoginMember(required = false) Long loginId) {
        String keyword = q.trim();
        Map<String, Object> result = new LinkedHashMap<>();
        if (keyword.startsWith("#")) {
            result.put("members", List.of());
            result.put("posts", postService.byHashtag(keyword.substring(1), page, 12, loginId));
        } else {
            result.put("members", memberService.search(keyword));
            result.put("posts", postService.byKeyword(keyword, page, 12, loginId));
        }
        return ApiResponse.ok(result);
    }
}
