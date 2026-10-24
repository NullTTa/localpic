package com.localpick.domain.comment;

import com.localpick.common.ApiResponse;
import com.localpick.config.LoginMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/api/posts/{postId}/comments")
    public ApiResponse<List<CommentDto.Response>> list(@PathVariable Long postId,
                                                       @LoginMember(required = false) Long loginId) {
        return ApiResponse.ok(commentService.list(postId, loginId));
    }

    @PostMapping("/api/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CommentDto.Response> write(@PathVariable Long postId,
                                                  @LoginMember Long loginId,
                                                  @RequestBody @Valid CommentDto.WriteRequest req) {
        return ApiResponse.ok(commentService.write(postId, loginId, req.getContent()));
    }

    @DeleteMapping("/api/comments/{commentId}")
    public ApiResponse<Void> delete(@PathVariable Long commentId, @LoginMember Long loginId) {
        commentService.delete(commentId, loginId);
        return ApiResponse.ok(null);
    }
}
