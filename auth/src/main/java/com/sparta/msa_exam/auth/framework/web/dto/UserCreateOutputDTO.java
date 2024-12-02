package com.sparta.msa_exam.auth.framework.web.dto;

import com.sparta.msa_exam.auth.application.domain.User;
import com.sparta.msa_exam.auth.domain.model.vo.UserRole;

public record UserCreateOutputDTO(
    Long userId,
    String username,
    UserRole userRole
) {

    public static UserCreateOutputDTO toDTO(User user) {
        return new UserCreateOutputDTO(
            user.getId(),
            user.getUsername(),
            user.getUserRole()
        );
    }
}
