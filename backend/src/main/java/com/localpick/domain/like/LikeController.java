package com.localpick.domain.like;

import com.localpick.common.ApiResponse;
import com.localpick.config.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/api/posts/{postId}/like")
    public ApiResponse<Map<String, Object>> toggle(@PathVariable Long postId, @LoginMember Long loginId) {
        return ApiResponse.ok(likeService.toggle(postId, loginId));
    }
}
