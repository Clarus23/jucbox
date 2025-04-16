package com.juc.jucbox.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class MemberLogin {
    @Id @Column(name="member_id")
    private Long id;

    @MapsId
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberDetail memberDetail;

    @Column(unique=true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private Role role = Role.USER;

    //== 생성 메서드 ==//
    public MemberLogin(String email, String password, MemberDetail memberDetail) {
        this.email = email;
        this.password = password;
        this.memberDetail = memberDetail; // 생성시 연관관계 주입.
    }

    //== 수정 메서드 ==//
    public void changeEmail(String newEmail) { this.email = newEmail; }
    public void changePassword(String newPassword) { this.password = newPassword; }

    public void nominatedAdmin() { this.role = Role.ADMIN; }
    public void terminatedAdmin() { this.role = Role.USER; }
}
