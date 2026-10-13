package com.localpick.domain.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MemberDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SignupRequest {
        @Email(message = "이메일 형식이 아닙니다.")
        @NotBlank(message = "이메일을 입력해주세요.")
        private String email;

        @Size(min = 8, max = 30, message = "비밀번호는 8~30자로 입력해주세요.")
        private String password;

        @Size(min = 2, max = 20, message = "닉네임은 2~20자로 입력해주세요.")
        private String nickname;

        private AccountType accountType;
        private String businessCategory;
        private String location;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class LoginRequest {
        @NotBlank(message = "이메일을 입력해주세요.")
        private String email;
        @NotBlank(message = "비밀번호를 입력해주세요.")
        private String password;
    }

    /** 게시물·댓글 작성자 등에 쓰는 짧은 회원 정보 */
    @Getter
    public static class Summary {
        private final Long id;
        private final String nickname;
        private final String profileImage;
        private final AccountType accountType;
        private final String businessCategory;

        public Summary(Member m) {
            this.id = m.getId();
            this.nickname = m.getNickname();
            this.profileImage = m.getProfileImage();
            this.accountType = m.getAccountType();
            this.businessCategory = m.getBusinessCategory();
        }
    }

    /** 프로필 화면 */
    @Getter
    public static class Profile {
        private final Long id;
        private final String email;
        private final String nickname;
        private final String bio;
        private final String profileImage;
        private final AccountType accountType;
        private final String businessCategory;
        private final String location;
        private final long postCount;
        private final long followerCount;
        private final long followingCount;
        private final boolean following;
        private final boolean me;

        public Profile(Member m, long postCount, long followerCount, long followingCount,
                       boolean following, boolean me) {
            this.id = m.getId();
            this.email = me ? m.getEmail() : null;
            this.nickname = m.getNickname();
            this.bio = m.getBio();
            this.profileImage = m.getProfileImage();
            this.accountType = m.getAccountType();
            this.businessCategory = m.getBusinessCategory();
            this.location = m.getLocation();
            this.postCount = postCount;
            this.followerCount = followerCount;
            this.followingCount = followingCount;
            this.following = following;
            this.me = me;
        }
    }
}
