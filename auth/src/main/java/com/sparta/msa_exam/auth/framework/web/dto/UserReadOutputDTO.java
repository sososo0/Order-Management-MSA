package com.sparta.msa_exam.auth.framework.web.dto;

import com.sparta.msa_exam.auth.application.domain.User;
import com.sparta.msa_exam.auth.domain.model.vo.UserRole;

public record UserReadOutputDTO(
    Long userId,
    String username,
    UserRole userRole
) {

    public static UserReadOutputDTO toDTO(User user) {
        return new UserReadOutputDTO(
            user.getId(),
            user.getUsername(),
            user.getUserRole()
        );
    }
}
