package com.localpick.domain.post;

import com.localpick.domain.member.MemberDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PostDto {

    /** multipart/form-data 로 받는 작성·수정 요청 */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class WriteRequest {
        @NotBlank(message = "내용을 입력해주세요.")
        @Size(max = 2000, message = "내용은 2000자까지 입력할 수 있습니다.")
        private String content;

        private PostType postType;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate eventStartDate;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate eventEndDate;

        private String linkUrl;

        private MultipartFile image;
    }

    @Getter
    public static class Response {
        private final Long id;
        private final MemberDto.Summary writer;
        private final String content;
        private final String imageUrl;
        private final PostType postType;
        private final LocalDate eventStartDate;
        private final LocalDate eventEndDate;
        private final String eventStatus;
        private final String linkUrl;
        private final long viewCount;
        private final long likeCount;
        private final long commentCount;
        private final boolean liked;
        private final boolean mine;
        private final List<String> hashtags;
        private final LocalDateTime createdAt;

        public Response(Post p, boolean liked, Long loginId, List<String> hashtags) {
            this.id = p.getId();
            this.writer = new MemberDto.Summary(p.getWriter());
            this.content = p.getContent();
            this.imageUrl = p.getImageUrl();
            this.postType = p.getPostType();
            this.eventStartDate = p.getEventStartDate();
            this.eventEndDate = p.getEventEndDate();
            this.eventStatus = p.eventStatus(LocalDate.now());
            this.linkUrl = p.getLinkUrl();
            this.viewCount = p.getViewCount();
            this.likeCount = p.getLikeCount();
            this.commentCount = p.getCommentCount();
            this.liked = liked;
            this.mine = loginId != null && p.isWrittenBy(loginId);
            this.hashtags = hashtags;
            this.createdAt = p.getCreatedAt();
        }
    }
}
