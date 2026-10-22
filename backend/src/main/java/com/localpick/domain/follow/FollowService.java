package com.localpick.domain.follow;

import com.localpick.common.BusinessException;
import com.localpick.domain.member.Member;
import com.localpick.domain.member.MemberRepository;
import com.localpick.domain.notification.NotificationService;
import com.localpick.domain.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;

    @Transactional
    public Map<String, Object> toggle(Long loginId, Long targetId) {
        if (loginId.equals(targetId)) {
            throw BusinessException.badRequest("자기 자신은 팔로우할 수 없습니다.");
        }
        Member target = memberRepository.findById(targetId)
                .orElseThrow(() -> BusinessException.notFound("회원"));

        boolean following;
        var existing = followRepository.findByFollowerIdAndFollowingId(loginId, targetId);
        if (existing.isPresent()) {
            followRepository.delete(existing.get());
            following = false;
        } else {
            followRepository.save(new Follow(memberRepository.getReferenceById(loginId), target));
            notificationService.send(targetId, loginId, NotificationType.FOLLOW, null);
            following = true;
        }
        followRepository.flush();
        return Map.<String, Object>of("following", following, "followerCount", followRepository.countByFollowingId(targetId));
    }
}
