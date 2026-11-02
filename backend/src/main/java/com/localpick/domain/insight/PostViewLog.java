package com.localpick.domain.insight;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 게시물 조회 기록 — 인사이트의 '일자별 조회수' 그래프를 그리기 위한 로그 */
@Getter
@Entity
@Table(name = "post_view_log", indexes = @Index(name = "idx_view_post_date", columnList = "post_id, viewed_at"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostViewLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "view_log_id")
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    /** 비로그인 조회는 null */
    @Column(name = "viewer_id")
    private Long viewerId;

    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;

    public PostViewLog(Long postId, Long viewerId, LocalDateTime viewedAt) {
        this.postId = postId;
        this.viewerId = viewerId;
        this.viewedAt = viewedAt;
    }
}
