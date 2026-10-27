package com.localpick.domain.post;

import com.localpick.common.ApiResponse;
import com.localpick.common.PageResponse;
import com.localpick.config.LoginMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 게시물 REST 컨트롤러.
 * (MVC 버전: @Controller + Model + "post/list" 뷰 이름 반환 → REST 버전: @RestController + JSON 반환)
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /** GET /api/posts?type=PROMOTION&page=0&size=10 */
    @GetMapping
    public ApiResponse<PageResponse<PostDto.Response>> feed(@RequestParam(required = false) PostType type,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            @LoginMember(required = false) Long loginId) {
        return ApiResponse.ok(postService.feed(type, page, size, loginId));
    }

    @GetMapping("/following")
    public ApiResponse<PageResponse<PostDto.Response>> following(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @LoginMember Long loginId) {
        return ApiResponse.ok(postService.followingFeed(loginId, page, size));
    }

    @GetMapping("/events/ongoing")
    public ApiResponse<List<PostDto.Response>> ongoingEvents(@LoginMember(required = false) Long loginId) {
        return ApiResponse.ok(postService.ongoingEvents(loginId));
    }

    @GetMapping("/member/{memberId}")
    public ApiResponse<PageResponse<PostDto.Response>> byMember(@PathVariable Long memberId,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "12") int size,
                                                                @LoginMember(required = false) Long loginId) {
        return ApiResponse.ok(postService.byWriter(memberId, page, size, loginId));
    }

    @GetMapping("/{postId}")
    public ApiResponse<PostDto.Response> detail(@PathVariable Long postId,
                                                @LoginMember(required = false) Long loginId) {
        return ApiResponse.ok(postService.detail(postId, loginId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PostDto.Response> create(@LoginMember Long loginId,
                                                @Valid @ModelAttribute PostDto.WriteRequest req) {
        return ApiResponse.ok("게시물이 등록되었습니다.", postService.create(loginId, req));
    }

    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostDto.Response> update(@PathVariable Long postId,
                                                @LoginMember Long loginId,
                                                @Valid @ModelAttribute PostDto.WriteRequest req) {
        return ApiResponse.ok("게시물이 수정되었습니다.", postService.update(postId, loginId, req));
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<Void> delete(@PathVariable Long postId, @LoginMember Long loginId) {
        postService.delete(postId, loginId);
        return ApiResponse.ok(null);
    }
}
