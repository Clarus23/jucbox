package com.juc.jucbox.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity @Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class MemberDetail {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="member_id")
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(unique=true, nullable = false)
    private String nickname;
    @Column(nullable = false)
    private String generation; // 기수
    private Long point = 0L;

    @OneToOne(mappedBy="memberDetail",cascade=CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval = true)
    private MemberLogin memberLogin;

    @OneToMany(mappedBy="member", fetch=FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy="member", fetch=FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    //== 생성 메서드 ==//
    public static MemberDetail create(String name, String nickname, String generation, String email, String password) {
        MemberDetail member = new MemberDetail();
        member.name = name;
        member.nickname = nickname;
        member.generation = generation;

        member.memberLogin = new MemberLogin(email, password, member); // 로그인 정보 생성과 동시에 연관관계 주입

        return member;
    }

    //== 수정 메서드 ==//
    public void changeName(String newName) { this.name = newName; }
    public void changeNickname(String newNickname) { this.nickname = newNickname; }

    //== 비지니스 로직 ==//
    public void incresePoint(Long point) {
        this.point += point;
    }
    public void decresePoint(Long point) {
        this.point -= point;
    }
}
