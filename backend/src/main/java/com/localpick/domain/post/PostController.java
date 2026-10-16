package com.localpick.domain.post;

import com.localpick.config.SessionConst;
import com.localpick.domain.like.LikeService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * [MVC 버전] 게시물 컨트롤러
 * - 화면(Thymeleaf 템플릿) 이름을 반환하고, 데이터는 Model 에 담아 서버에서 HTML 을 만든다.
 * - 좋아요 하나를 눌러도 redirect → 페이지 전체를 다시 그린다.
 */
@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final LikeService likeService;

    @GetMapping
    public String list(@RequestParam(required = false) PostType type,
                       @RequestParam(defaultValue = "0") int page,
                       HttpSession session, Model model) {
        Long loginId = (Long) session.getAttribute(SessionConst.LOGIN_MEMBER_ID);
        model.addAttribute("posts", postService.feed(type, page, 10, loginId));
        model.addAttribute("type", type);
        return "post/list";
    }

    @GetMapping("/{postId}")
    public String detail(@PathVariable Long postId, HttpSession session, Model model) {
        Long loginId = (Long) session.getAttribute(SessionConst.LOGIN_MEMBER_ID);
        model.addAttribute("post", postService.detail(postId, loginId));
        return "post/detail";
    }

    @GetMapping("/new")
    public String writeForm(HttpSession session, Model model) {
        if (session.getAttribute(SessionConst.LOGIN_MEMBER_ID) == null) {
            return "redirect:/login";
        }
        model.addAttribute("form", new PostDto.WriteRequest());
        return "post/write";
    }

    @PostMapping("/new")
    public String write(@Valid @ModelAttribute("form") PostDto.WriteRequest form, BindingResult bindingResult,
                        HttpSession session, RedirectAttributes ra) {
        Long loginId = (Long) session.getAttribute(SessionConst.LOGIN_MEMBER_ID);
        if (loginId == null) return "redirect:/login";
        if (bindingResult.hasErrors()) return "post/write";

        PostDto.Response saved = postService.create(loginId, form);
        ra.addAttribute("postId", saved.getId());
        return "redirect:/posts/{postId}";
    }

    @PostMapping("/{postId}/like")
    public String like(@PathVariable Long postId, HttpSession session) {
        Long loginId = (Long) session.getAttribute(SessionConst.LOGIN_MEMBER_ID);
        if (loginId == null) return "redirect:/login";
        likeService.toggle(postId, loginId);
        return "redirect:/posts/" + postId;   // 좋아요 후 상세 페이지 전체 새로고침
    }

    @PostMapping("/{postId}/delete")
    public String delete(@PathVariable Long postId, HttpSession session) {
        Long loginId = (Long) session.getAttribute(SessionConst.LOGIN_MEMBER_ID);
        postService.delete(postId, loginId);
        return "redirect:/posts";
    }
}
