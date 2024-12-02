package com.sparta.msa_exam.auth.framework.web.controller;

import com.sparta.msa_exam.auth.application.domain.User;
import com.sparta.msa_exam.auth.application.domain.UserForCreate;
import com.sparta.msa_exam.auth.application.usecase.UserUseCase;
import com.sparta.msa_exam.auth.framework.web.dto.UserCreateInputDTO;
import com.sparta.msa_exam.auth.framework.web.dto.UserCreateOutputDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class UserCommandController {

    private final UserUseCase userUseCase;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/sign-up")
    public UserCreateOutputDTO createUser(
        @Valid @RequestBody UserCreateInputDTO userCreateInputDTO
    ) {

        UserForCreate userForCreate = UserCreateInputDTO.toDomain(
            userCreateInputDTO
        );
        User user = userUseCase.createUser(userForCreate);

        return UserCreateOutputDTO.toDTO(user);
    }

}
