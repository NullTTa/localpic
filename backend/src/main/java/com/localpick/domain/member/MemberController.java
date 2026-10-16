package com.localpick.domain.member;

import com.localpick.common.ApiResponse;
import com.localpick.config.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/{memberId}")
    public ApiResponse<MemberDto.Profile> profile(@PathVariable Long memberId,
                                                  @LoginMember(required = false) Long loginId) {
        return ApiResponse.ok(memberService.profile(memberId, loginId));
    }

    @PutMapping(value = "/me", consumes = "multipart/form-data")
    public ApiResponse<MemberDto.Profile> updateMe(@LoginMember Long loginId,
                                                   @RequestParam(required = false) String nickname,
                                                   @RequestParam(required = false) String bio,
                                                   @RequestParam(required = false) String location,
                                                   @RequestPart(required = false) MultipartFile image) {
        return ApiResponse.ok(memberService.updateMe(loginId, nickname, bio, location, image));
    }

    @GetMapping("/recommend")
    public ApiResponse<List<MemberDto.Summary>> recommend(@LoginMember(required = false) Long loginId) {
        return ApiResponse.ok(memberService.recommendStores(loginId));
    }
}
