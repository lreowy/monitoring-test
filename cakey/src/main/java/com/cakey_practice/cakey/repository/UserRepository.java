package com.cakey_practice.cakey.repository;

import com.cakey_practice.cakey.auth.SocialType;
import com.cakey_practice.cakey.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySocialTypeAndSocialId(final SocialType socialType, final Long socialId);
}
