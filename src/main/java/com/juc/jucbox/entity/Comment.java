package com.juc.jucbox.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {
    @Id @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="comment_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberDetail member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    //== 연관관계 메서드 ==//
    private void setMember(MemberDetail member) {
        this.member = member;
        member.getComments().add(this);
    }

    private void setPost(Post post) {
        this.post = post;
        post.getComments().add(this);
    }

    //== 생성 메서드 ==//
    public static Comment create(String content, MemberDetail creator, Post post) {
        Comment comment = new Comment();

        comment.content = content;

        comment.setMember(creator);
        comment.setPost(post);

        return comment;
    }

    //== 수정 메서드 ==//
    public void changeContent(String content) { this.content = content; }
}
