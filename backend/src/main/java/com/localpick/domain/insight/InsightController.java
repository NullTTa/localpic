package com.localpick.domain.insight;

import com.localpick.common.ApiResponse;
import com.localpick.config.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class InsightController {

    private final InsightService insightService;

    @GetMapping("/api/insights/me")
    public ApiResponse<Map<String, Object>> myInsight(@LoginMember Long loginId) {
        return ApiResponse.ok(insightService.myInsight(loginId));
    }
}
