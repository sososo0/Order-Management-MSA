package com.sparta.msa_exam.auth.application.domain;

import com.sparta.msa_exam.auth.domain.model.vo.UserRole;
import lombok.Getter;

@Getter
public class User {
    private Long id;
    private String username;
    private UserRole userRole;

    public User(
        Long id,
        String username,
        UserRole userRole
    ) {
        this.id = id;
        this.username = username;
        this.userRole = userRole;
    }
}
