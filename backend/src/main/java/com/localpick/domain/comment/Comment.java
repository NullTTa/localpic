package com.localpick.domain.comment;

import com.localpick.common.BaseTimeEntity;
import com.localpick.domain.member.Member;
import com.localpick.domain.post.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member writer;

    @Column(nullable = false, length = 500)
    private String content;

    public Comment(Post post, Member writer, String content) {
        this.post = post;
        this.writer = writer;
        this.content = content;
    }
}
