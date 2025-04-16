package com.juc.jucbox.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@Entity @Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {
    @Id @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="post_id")
    private Long id;

    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private MemberDetail member;

    @OneToMany(mappedBy="post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files = new ArrayList<>();

    @OneToMany(mappedBy="post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    //== 연관관계 메서드 ==//
    private void setMember(MemberDetail member) {
        this.member = member;

        if (!member.getPosts().contains(this))
            member.getPosts().add(this);
    }

    //== 생성 메서드 ==//
    public static Post create(String title, String content, MemberDetail creator, List<File> files) {
        Post post = new Post();
        post.title = title;
        post.content = content;

        post.setMember(creator);

        for(File file : files) file.setPost(post);

        return post;
    }

    //== 수정 메서드 ==//
    public void changePost(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void changeFile(List<File> newFiles) {
        Map<String, File> newFileMap = newFiles.stream()
                .collect(Collectors.toMap(File::getNewFilename, f-> f));

        // 기존파일 중 제거 대상 찾기
        Iterator<File> iter = this.files.iterator();
        while(iter.hasNext()) {
            File existingFile = iter.next();
            if(!newFileMap.containsKey(existingFile.getNewFilename())) {
                iter.remove();
            }
        }

        // 기존에 없는 새 파일 추가
        Set<String> existingNames = this.files.stream()
                .map(File::getNewFilename)
                .collect(Collectors.toSet());

        for(File file : newFiles) {
            if(!existingNames.contains(file.getNewFilename()))
                file.setPost(this);
        }
    }
}
