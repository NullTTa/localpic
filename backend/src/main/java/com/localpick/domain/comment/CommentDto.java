package com.localpick.domain.comment;

import com.localpick.domain.member.MemberDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public class CommentDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class WriteRequest {
        @NotBlank(message = "댓글을 입력해주세요.")
        @Size(max = 500, message = "댓글은 500자까지 입력할 수 있습니다.")
        private String content;
    }

    @Getter
    public static class Response {
        private final Long id;
        private final MemberDto.Summary writer;
        private final String content;
        private final boolean mine;
        private final LocalDateTime createdAt;

        public Response(Comment c, Long loginId) {
            this.id = c.getId();
            this.writer = new MemberDto.Summary(c.getWriter());
            this.content = c.getContent();
            this.mine = loginId != null && c.getWriter().getId().equals(loginId);
            this.createdAt = c.getCreatedAt();
        }
    }
}
