package com.localpick.domain.member;

import com.localpick.common.BusinessException;
import com.localpick.config.FileStore;
import com.localpick.domain.follow.FollowRepository;
import com.localpick.domain.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    private final FileStore fileStore;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public Long signup(MemberDto.SignupRequest req) {
        if (memberRepository.existsByEmail(req.getEmail())) {
            throw BusinessException.badRequest("이미 가입된 이메일입니다.");
        }
        if (memberRepository.existsByNickname(req.getNickname())) {
            throw BusinessException.badRequest("이미 사용 중인 닉네임입니다.");
        }
        Member member = Member.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .nickname(req.getNickname())
                .accountType(req.getAccountType())
                .businessCategory(req.getBusinessCategory())
                .location(req.getLocation())
                .build();
        return memberRepository.save(member).getId();
    }

    public Member login(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> BusinessException.badRequest("이메일 또는 비밀번호가 올바르지 않습니다."));
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw BusinessException.badRequest("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        return member;
    }

    public Member get(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> BusinessException.notFound("회원"));
    }

    public MemberDto.Profile profile(Long targetId, Long loginId) {
        Member target = get(targetId);
        boolean me = target.getId().equals(loginId);
        boolean following = loginId != null && !me
                && followRepository.existsByFollowerIdAndFollowingId(loginId, targetId);
        return new MemberDto.Profile(
                target,
                postRepository.countByWriterId(targetId),
                followRepository.countByFollowingId(targetId),
                followRepository.countByFollowerId(targetId),
                following,
                me);
    }

    @Transactional
    public MemberDto.Profile updateMe(Long loginId, String nickname, String bio, String location,
                                      MultipartFile image) {
        Member me = get(loginId);
        if (nickname != null && !nickname.equals(me.getNickname()) && memberRepository.existsByNickname(nickname)) {
            throw BusinessException.badRequest("이미 사용 중인 닉네임입니다.");
        }
        me.updateProfile(nickname, bio, location, fileStore.store(image));
        return profile(loginId, loginId);
    }

    /** 팔로워가 많은 사업자 계정 순으로 추천 (로그인 시 이미 팔로우한 계정은 제외) */
    public List<MemberDto.Summary> recommendStores(Long loginId) {
        return memberRepository.findByAccountType(AccountType.BUSINESS).stream()
                .filter(m -> loginId == null || (!m.getId().equals(loginId)
                        && !followRepository.existsByFollowerIdAndFollowingId(loginId, m.getId())))
                .sorted(Comparator.comparingLong((Member m) -> followRepository.countByFollowingId(m.getId())).reversed())
                .limit(5)
                .map(MemberDto.Summary::new)
                .toList();
    }

    public List<MemberDto.Summary> search(String keyword) {
        return memberRepository.findTop10ByNicknameContainingIgnoreCase(keyword).stream()
                .map(MemberDto.Summary::new).toList();
    }
}
