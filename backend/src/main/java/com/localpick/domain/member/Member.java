package com.localpick.domain.member;

import com.localpick.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 30)
    private String nickname;

    @Column(length = 300)
    private String bio;

    @Column(name = "profile_image")
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 20)
    private AccountType accountType;

    /** 사업자 계정일 때 업종 (카페, 베이커리, 꽃집 ...) */
    @Column(name = "business_category", length = 30)
    private String businessCategory;

    /** 사업자 계정일 때 가게 위치 (예: 인천 부평구) */
    @Column(length = 100)
    private String location;

    @Builder
    public Member(String email, String password, String nickname, String bio, String profileImage,
                  AccountType accountType, String businessCategory, String location) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.bio = bio;
        this.profileImage = profileImage;
        this.accountType = accountType == null ? AccountType.PERSONAL : accountType;
        this.businessCategory = businessCategory;
        this.location = location;
    }

    public void updateProfile(String nickname, String bio, String location, String profileImage) {
        if (nickname != null && !nickname.isBlank()) this.nickname = nickname;
        this.bio = bio;
        this.location = location;
        if (profileImage != null) this.profileImage = profileImage;
    }

    public boolean isBusiness() {
        return accountType == AccountType.BUSINESS;
    }
}
