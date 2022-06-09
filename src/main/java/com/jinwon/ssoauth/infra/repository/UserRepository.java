package com.jinwon.ssoauth.infra.repository;

import com.jinwon.ssoauth.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 사용자 Repository
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUid(String uid);

}