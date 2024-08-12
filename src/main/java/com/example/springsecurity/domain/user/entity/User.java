package com.example.springsecurity.domain.user.entity;

import com.example.springsecurity.domain.user.converter.AuthorityListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Convert(converter = AuthorityListConverter.class)
    @Column
    private final List<AuthorityEnum> authorities = new ArrayList<>();

    public User(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        addAuthority(AuthorityEnum.USER);
    }

    public void updateAuthority() {
        addAuthority(AuthorityEnum.ADMIN);
    }

    private void addAuthority(AuthorityEnum authority) {

        if (!authorities.contains(authority)) {
            authorities.add(authority);
        }

    }
}
