package com.localpick.domain.follow;

import com.localpick.common.ApiResponse;
import com.localpick.config.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/api/members/{memberId}/follow")
    public ApiResponse<Map<String, Object>> toggle(@PathVariable Long memberId, @LoginMember Long loginId) {
        return ApiResponse.ok(followService.toggle(loginId, memberId));
    }
}
