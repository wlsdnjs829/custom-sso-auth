package com.jinwon.ssoauth.infra.repository;

import com.jinwon.ssoauth.domain.entity.profile.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 프로필 Repository
 */
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByEmail(String email);

}