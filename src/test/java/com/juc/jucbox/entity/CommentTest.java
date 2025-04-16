package com.juc.jucbox.entity;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CommentTest {
    @Autowired EntityManager em;

    private MemberDetail member;
    private Post post;

    @BeforeEach
    void setUp() {
        member = MemberDetail.create("name", "nickname", "52", "test@email.com", "pwd123");
        em.persist(member);

        post = Post.create("title", "content", member, new ArrayList<>());
        em.persist(post);

        em.flush(); em.clear();
    }

    @Test
    public void 댓글_작성() throws Exception {
        //given
        //when
        Comment comment = Comment.create("content", member, post);
        em.persist(comment);

        em.flush(); em.clear();

        Comment foundComment = em.find(Comment.class, comment.getId());

        //then
        MemberDetail foundMember = em.find(MemberDetail.class, member.getId());
        Post foundPost = em.find(Post.class, post.getId());

        assertThat(foundComment.getContent()).isEqualTo(comment.getContent());
        assertThat(foundComment.getMember().getId()).isEqualTo(member.getId());
        assertThat(foundComment.getPost().getId()).isEqualTo(post.getId());

        assertThat(foundMember.getComments().size()).isEqualTo(1);
        assertThat(foundPost.getComments().size()).isEqualTo(1);
    }

    @Test
    public void 댓글_수정() throws Exception {
        //given
        Comment comment = Comment.create("content", member, post);
        em.persist(comment);

        em.flush(); em.clear();

        //when
        Comment foundComment = em.find(Comment.class, comment.getId());
        foundComment.changeContent("newContent");

        //then
        assertThat(foundComment.getContent()).isEqualTo("newContent");
    }

    @Test
    public void 댓글_삭제() throws Exception {
        //given
        Comment comment = Comment.create("content", member, post);
        em.persist(comment);

        em.flush(); em.clear();

        //when
        Comment foundComment = em.find(Comment.class, comment.getId());
        em.remove(foundComment);

        em.flush(); em.clear();

        //then
        MemberDetail foundMember = em.find(MemberDetail.class, member.getId());
        Post foundPost = em.find(Post.class, post.getId());

        assertThat(em.find(Comment.class, comment.getId())).isNull();
        assertThat(foundMember.getComments().size()).isEqualTo(0);
        assertThat(foundPost.getComments().size()).isEqualTo(0);
    }

    @Test
    public void 게시글_삭제시_댓글_삭제() throws Exception {
        //given
        Comment comment = Comment.create("content", member, post);
        em.persist(comment);

        em.flush(); em.clear();

        //when
        Post foundPost = em.find(Post.class, post.getId());
        em.remove(foundPost);

        em.flush(); em.clear();

        //then
        MemberDetail foundMember = em.find(MemberDetail.class, member.getId());

        assertThat(em.find(Comment.class, comment.getId())).isNull();
        assertThat(foundMember.getComments().size()).isEqualTo(0);
    }
}