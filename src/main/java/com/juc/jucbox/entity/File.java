package com.juc.jucbox.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class File {
    @Id @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="file_id")
    private Long id;

    private String originalFilename;
    private String newFilename;
    private String fileExtension;
    private Long fileSize;
    private String saveFilePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="post_id")
    private Post post;

    //== 연관관계 메서드 ==//
    public void setPost(Post post) {
        this.post = post;

        if(!post.getFiles().contains(this))
            post.getFiles().add(this);
    }

    //== 생성 메서드 ==//
    public static File create(String originalFilename, String newFilename, String fileExtension, Long fileSize, String saveFilePath) {
        File file = new File();

        file.originalFilename = originalFilename;
        file.newFilename = newFilename;
        file.fileExtension = fileExtension;
        file.fileSize = fileSize;
        file.saveFilePath = saveFilePath;

        return file;
    }
}
