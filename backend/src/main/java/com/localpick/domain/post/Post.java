package com.localpick.domain.post;

import com.localpick.common.BaseTimeEntity;
import com.localpick.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "post", indexes = {
        @Index(name = "idx_post_writer", columnList = "member_id"),
        @Index(name = "idx_post_type", columnList = "post_type")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member writer;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false, length = 20)
    private PostType postType;

    @Column(name = "event_start_date")
    private LocalDate eventStartDate;

    @Column(name = "event_end_date")
    private LocalDate eventEndDate;

    /** 홍보 게시물의 가게/상품 링크 (네이버 플레이스, 스마트스토어 등) */
    @Column(name = "link_url", length = 500)
    private String linkUrl;

    @Column(name = "view_count", nullable = false)
    private long viewCount;

    @Column(name = "like_count", nullable = false)
    private long likeCount;

    @Column(name = "comment_count", nullable = false)
    private long commentCount;

    @Builder
    public Post(Member writer, String content, String imageUrl, PostType postType,
                LocalDate eventStartDate, LocalDate eventEndDate, String linkUrl) {
        this.writer = writer;
        this.content = content;
        this.imageUrl = imageUrl;
        this.postType = postType == null ? PostType.DAILY : postType;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.linkUrl = linkUrl;
    }

    public void update(String content, PostType postType, LocalDate eventStartDate,
                       LocalDate eventEndDate, String linkUrl, String newImageUrl) {
        this.content = content;
        this.postType = postType == null ? this.postType : postType;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.linkUrl = linkUrl;
        if (newImageUrl != null) this.imageUrl = newImageUrl;
    }

    public boolean isWrittenBy(Long memberId) {
        return writer.getId().equals(memberId);
    }

    /** 이벤트 게시물의 진행 상태: UPCOMING / ONGOING / ENDED (이벤트가 아니면 null) */
    public String eventStatus(LocalDate today) {
        if (postType != PostType.EVENT || eventStartDate == null || eventEndDate == null) return null;
        if (today.isBefore(eventStartDate)) return "UPCOMING";
        if (today.isAfter(eventEndDate)) return "ENDED";
        return "ONGOING";
    }
}
