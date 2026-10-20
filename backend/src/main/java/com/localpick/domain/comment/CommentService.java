package com.localpick.domain.comment;

import com.localpick.common.BusinessException;
import com.localpick.domain.member.MemberRepository;
import com.localpick.domain.notification.NotificationService;
import com.localpick.domain.notification.NotificationType;
import com.localpick.domain.post.Post;
import com.localpick.domain.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;

    public List<CommentDto.Response> list(Long postId, Long loginId) {
        return commentRepository.findByPostIdOrderByIdAsc(postId).stream()
                .map(c -> new CommentDto.Response(c, loginId))
                .toList();
    }

    @Transactional
    public CommentDto.Response write(Long postId, Long loginId, String content) {
        Post post = postRepository.findById(postId).orElseThrow(() -> BusinessException.notFound("게시물"));
        Comment saved = commentRepository.save(
                new Comment(post, memberRepository.getReferenceById(loginId), content));
        Long writerId = post.getWriter().getId();
        postRepository.addCommentCount(postId, 1);
        notificationService.send(writerId, loginId, NotificationType.COMMENT, postId);
        return new CommentDto.Response(commentRepository.findById(saved.getId()).orElseThrow(), loginId);
    }

    @Transactional
    public void delete(Long commentId, Long loginId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> BusinessException.notFound("댓글"));
        if (!comment.getWriter().getId().equals(loginId)) {
            throw BusinessException.forbidden();
        }
        Long postId = comment.getPost().getId();
        commentRepository.delete(comment);
        postRepository.addCommentCount(postId, -1);
    }
}
