package com.sparta.msa_exam.auth.application.domain;

import com.sparta.msa_exam.auth.domain.model.vo.UserRole;
import lombok.Getter;

@Getter
public class User {
    private Long id;
    private String username;
    private String password;
    private UserRole userRole;

    public User(
        Long id,
        String username,
        String password,
        UserRole userRole
    ) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.userRole = userRole;
    }
}
