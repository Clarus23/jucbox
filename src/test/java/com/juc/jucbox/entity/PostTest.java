package com.juc.jucbox.entity;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PostTest {
    @Autowired EntityManager em;

    private MemberDetail member;

    @BeforeEach
    public void setUp() {
        member = MemberDetail.create("name", "nickname", "52", "test@email.com", "pwd123");
        em.persist(member);
        em.flush(); em.clear();
    }

    @Test
    public void 게시글_등록_파일X() throws Exception {
        //given
        List<File> files = new ArrayList<>();

        //when
        Post post = Post.create("title", "content", member, files);
        em.persist(post);

        em.flush(); em.clear();

        Post foundPost = em.find(Post.class, post.getId());

        //then
        assertThat(foundPost.getId()).isEqualTo(post.getId());
        assertThat(foundPost.getTitle()).isEqualTo(post.getTitle());
        assertThat(foundPost.getContent()).isEqualTo(post.getContent());
        assertThat(foundPost.getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    public void 게시글_등록_파일O() throws Exception {
        //given
        File file1 = File.create("filename1", "newFilename1", "jpg", 1024L, "save/file/path/1");
        File file2 = File.create("filename2", "newFilename2", "jpg", 2048L, "save/file/path/2");
        List<File> files = List.of(file1, file2);

        //when
        Post post = Post.create("title", "content", member, files);
        em.persist(post);

        em.flush(); em.clear();

        Post foundPost = em.find(Post.class, post.getId());

        //then
        List<String> newFilenames = foundPost.getFiles()
                .stream().map(File::getNewFilename).collect(Collectors.toList());

        assertThat(newFilenames).containsExactlyInAnyOrder("newFilename1", "newFilename2");
    }

    @Test
    public void 게시글_수정() throws Exception {
        //given
        File file1 = File.create("filename1", "newFilename1", "jpg", 1024L, "save/file/path/1");
        File file2 = File.create("filename2", "newFilename2", "jpg", 2048L, "save/file/path/2");
        List<File> files = List.of(file1, file2);

        Post post = Post.create("title", "content", member, files);
        em.persist(post);

        em.flush(); em.clear();

        //when
        Post foundPost = em.find(Post.class, post.getId());
        foundPost.changePost("newTitle", "newContent");

        List<File> newFiles = new ArrayList<>(); // file1은 유지, file2는 삭제, file3, file4는 추가
        newFiles.add(File.create(file1.getOriginalFilename(), file1.getNewFilename(), file1.getFileExtension(), file1.getFileSize(), file1.getSaveFilePath()));
        newFiles.add(File.create("filename3", "newFilename3", "jpg", 3072L, "save/file/path/3"));
        newFiles.add(File.create("filename4", "newFilename4", "jpg", 4096L, "save/file/path/4"));
        foundPost.changeFile(newFiles);

        em.flush(); em.clear();

        //then
        Post updatedPost = em.find(Post.class, post.getId());
        List<String> newFilenames = updatedPost.getFiles().stream()
                .map(File::getNewFilename)
                .toList();

        assertThat(updatedPost.getTitle()).isEqualTo("newTitle");
        assertThat(updatedPost.getContent()).isEqualTo("newContent");
        assertThat(updatedPost.getFiles().size()).isEqualTo(3);
        assertThat(newFilenames).containsExactlyInAnyOrder("newFilename1", "newFilename3", "newFilename4");
    }

    @Test
    public void 게시글_삭제() throws Exception {
        //given
        File file1 = File.create("filename1", "newFilename1", "jpg", 1024L, "save/file/path/1");
        File file2 = File.create("filename2", "newFilename2", "jpg", 2048L, "save/file/path/2");
        List<File> files = List.of(file1, file2);

        Post post = Post.create("title", "content", member, files);
        em.persist(post);

        em.flush(); em.clear();

        //when
        Post foundPost = em.find(Post.class, post.getId());
        List<Long> fileIds = foundPost.getFiles().stream()
                        .map(File::getId)
                        .toList();
        em.remove(foundPost);

        em.flush(); em.clear();

        //then
        assertThat(em.find(Post.class, post.getId())).isNull();
        assertThat(em.find(File.class, fileIds.get(0))).isNull();
        assertThat(em.find(File.class, fileIds.get(1))).isNull();
    }
}