package com.sparta.msa_exam.auth.framework.web.dto;

import com.sparta.msa_exam.auth.application.domain.UserForAuthenticate;

public record UserAuthenticateInputDTO(
    String username,
    String password
) {

    public static UserForAuthenticate toDomain(
        UserAuthenticateInputDTO userAuthenticateInputDTO
    ) {
        return new UserForAuthenticate(
            userAuthenticateInputDTO.username(),
            userAuthenticateInputDTO.password()
        );
    }
}
