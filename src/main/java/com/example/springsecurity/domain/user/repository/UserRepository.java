package com.example.springsecurity.domain.user.repository;

import com.example.springsecurity.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByNickname(String nickname);

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
}
