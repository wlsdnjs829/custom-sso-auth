package com.jinwon.ssoauth.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 회원 Repository
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

}