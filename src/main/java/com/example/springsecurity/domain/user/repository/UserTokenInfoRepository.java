package com.example.springsecurity.domain.user.repository;

import com.example.springsecurity.domain.user.entity.User;
import com.example.springsecurity.domain.user.entity.UserTokenInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTokenInfoRepository extends JpaRepository<UserTokenInfo, Long> {

    void deleteByUser(User user);

    Optional<UserTokenInfo> findByUser(User user);

    Optional<UserTokenInfo> findByAccessToken(String token);
}
