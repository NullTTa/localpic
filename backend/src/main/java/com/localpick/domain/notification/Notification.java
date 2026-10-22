package com.localpick.domain.notification;

import com.localpick.common.BaseTimeEntity;
import com.localpick.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "notification", indexes = @Index(name = "idx_noti_receiver", columnList = "receiver_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationType type;

    /** 좋아요·댓글 알림일 때 대상 게시물 id (팔로우 알림이면 null) */
    @Column(name = "post_id")
    private Long postId;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    public Notification(Member receiver, Member sender, NotificationType type, Long postId) {
        this.receiver = receiver;
        this.sender = sender;
        this.type = type;
        this.postId = postId;
    }
}
