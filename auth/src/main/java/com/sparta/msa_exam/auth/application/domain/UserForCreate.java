package com.sparta.msa_exam.auth.application.domain;

import com.sparta.msa_exam.auth.domain.model.vo.UserRole;

public record UserForCreate(
    String username,
    String password,
    UserRole userRole
) {

}
