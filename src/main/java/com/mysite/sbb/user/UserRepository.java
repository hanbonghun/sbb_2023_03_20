package com.mysite.sbb.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<SiteUser,Long> {
    Optional<SiteUser> findByusername(String username);
    Optional<SiteUser> findByEmail(@Param("email") String email);
    Optional<SiteUser> findByNickname(@Param("nickname") String nickname);
    Optional<SiteUser> findByGoogleId(@Param("google_id") String google_id);
}
