package com.localpick.domain.member;

import com.localpick.common.ApiResponse;
import com.localpick.config.LoginMember;
import com.localpick.config.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Long> signup(@RequestBody @Valid MemberDto.SignupRequest req) {
        return ApiResponse.ok("회원가입이 완료되었습니다.", memberService.signup(req));
    }

    @PostMapping("/login")
    public ApiResponse<MemberDto.Profile> login(@RequestBody @Valid MemberDto.LoginRequest req,
                                                HttpServletRequest request) {
        Member member = memberService.login(req.getEmail(), req.getPassword());
        // 세션 고정 공격 방지를 위해 기존 세션을 버리고 새로 만든다
        HttpSession old = request.getSession(false);
        if (old != null) old.invalidate();
        request.getSession(true).setAttribute(SessionConst.LOGIN_MEMBER_ID, member.getId());
        return ApiResponse.ok(memberService.profile(member.getId(), member.getId()));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        return ApiResponse.ok(null);
    }

    /** 새로고침해도 로그인 상태를 유지하기 위해 React 가 처음에 호출 */
    @GetMapping("/me")
    public ApiResponse<MemberDto.Profile> me(@LoginMember(required = false) Long loginId) {
        return ApiResponse.ok(loginId == null ? null : memberService.profile(loginId, loginId));
    }
}
