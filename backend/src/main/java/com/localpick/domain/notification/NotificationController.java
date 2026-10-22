package com.localpick.domain.notification;

import com.localpick.common.ApiResponse;
import com.localpick.config.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list(@LoginMember Long loginId) {
        return ApiResponse.ok(notificationService.list(loginId));
    }

    @GetMapping("/unread-count")
    public ApiResponse<Long> unreadCount(@LoginMember Long loginId) {
        return ApiResponse.ok(notificationService.unreadCount(loginId));
    }

    @PatchMapping("/read-all")
    public ApiResponse<Integer> readAll(@LoginMember Long loginId) {
        return ApiResponse.ok(notificationService.readAll(loginId));
    }
}
