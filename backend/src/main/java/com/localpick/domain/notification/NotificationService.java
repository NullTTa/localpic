package com.localpick.domain.notification;

import com.localpick.domain.member.MemberDto;
import com.localpick.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    /** 내가 내 글에 좋아요/댓글 단 경우는 알림을 만들지 않는다 */
    @Transactional
    public void send(Long receiverId, Long senderId, NotificationType type, Long postId) {
        if (receiverId.equals(senderId)) return;
        notificationRepository.save(new Notification(
                memberRepository.getReferenceById(receiverId),
                memberRepository.getReferenceById(senderId),
                type, postId));
    }

    public List<Map<String, Object>> list(Long loginId) {
        return notificationRepository.findTop30ByReceiverIdOrderByIdDesc(loginId).stream()
                .map(n -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", n.getId());
                    m.put("type", n.getType());
                    m.put("sender", new MemberDto.Summary(n.getSender()));
                    m.put("postId", n.getPostId());
                    m.put("read", n.isRead());
                    m.put("createdAt", n.getCreatedAt());
                    return m;
                })
                .toList();
    }

    public long unreadCount(Long loginId) {
        return notificationRepository.countByReceiverIdAndReadFalse(loginId);
    }

    @Transactional
    public int readAll(Long loginId) {
        return notificationRepository.markAllRead(loginId);
    }
}
