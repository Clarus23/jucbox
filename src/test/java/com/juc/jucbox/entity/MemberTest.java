package com.juc.jucbox.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class MemberTest {
    @Autowired EntityManager em;

    @Test
    public void 회원가입() throws Exception {
        //given
        String email = "test@email.com";
        String password = "password";
        String name = "name";
        String nickname = "nickname";
        String generation = "52";

        //when
        MemberDetail member = MemberDetail.create(name, nickname, generation, email, password);
        em.persist(member); // MemberLogin entity는 cascade로 자동 저장

        em.flush(); em.clear();

        MemberDetail foundDetail = em.find(MemberDetail.class, member.getId());
        MemberLogin foundLogin = em.find(MemberLogin.class, member.getId());

        //then
        assertThat(foundDetail.getId()).isEqualTo(member.getId());
        assertThat(foundDetail.getName()).isEqualTo(member.getName());
        assertThat(foundDetail.getNickname()).isEqualTo(member.getNickname());
        assertThat(foundDetail.getGeneration()).isEqualTo(member.getGeneration());
        assertThat(foundDetail.getPoint()).isEqualTo(0);

        assertThat(foundLogin.getId()).isEqualTo(foundDetail.getId());
        assertThat(foundLogin.getEmail()).isEqualTo(member.getMemberLogin().getEmail());
        assertThat(foundLogin.getPassword()).isEqualTo(member.getMemberLogin().getPassword());
        assertThat(foundLogin.getRole()).isEqualTo(Role.USER);
    }

    @Test
    public void 이메일_중복_예외() throws Exception {
        //given
        MemberDetail member1 = MemberDetail.create("A", "nick1", "12기", "same@email.com", "pwd");
        MemberDetail member2 = MemberDetail.create("B", "nick2", "13기", "same@email.com", "pwd");

        //when
        em.persist(member1);

        //then
        assertThrows(PersistenceException.class, () -> {
            em.persist(member2);
            em.flush();  // 예외 발생
        });
    }

    @Test
    public void 회원정보_수정() throws Exception {
        //given
        MemberDetail member = MemberDetail.create("name", "nickname", "52", "test@email.com", "pwd123");
        em.persist(member);

        em.flush(); em.clear();

        //when
        MemberDetail found = em.find(MemberDetail.class, member.getId());
        found.changeName("newName");
        found.changeNickname("newNickname");

        found.getMemberLogin().changeEmail("new@email.com");
        found.getMemberLogin().changePassword("newPassword");
        found.getMemberLogin().nominatedAdmin();

        em.flush(); em.clear();

        //then
        MemberDetail updated = em.find(MemberDetail.class, member.getId());
        assertThat(updated.getName()).isEqualTo("newName");
        assertThat(updated.getNickname()).isEqualTo("newNickname");
        assertThat(updated.getMemberLogin().getEmail()).isEqualTo("new@email.com");
        assertThat(updated.getMemberLogin().getPassword()).isEqualTo("newPassword");
        assertThat(updated.getMemberLogin().getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    public void 회원정보_삭제() throws Exception {
        //given
        MemberDetail member = MemberDetail.create("name", "nickname", "52", "test@email.com", "pwd123");
        em.persist(member);
        Long id = member.getId();

        //when
        em.remove(member);
        em.flush(); em.clear();

        //then
        MemberDetail foundDetail = em.find(MemberDetail.class, id);
        MemberLogin foundLogin = em.find(MemberLogin.class, member.getId());

        assertThat(foundDetail).isNull();
        assertThat(foundLogin).isNull();
    }
}