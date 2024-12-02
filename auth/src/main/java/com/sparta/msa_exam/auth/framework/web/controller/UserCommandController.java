package com.sparta.msa_exam.auth.framework.web.controller;

import com.sparta.msa_exam.auth.application.domain.User;
import com.sparta.msa_exam.auth.application.domain.UserForAuthenticate;
import com.sparta.msa_exam.auth.application.domain.UserForCreate;
import com.sparta.msa_exam.auth.application.usecase.UserUseCase;
import com.sparta.msa_exam.auth.application.util.JwtTokenProvider;
import com.sparta.msa_exam.auth.framework.web.dto.TokenDTO;
import com.sparta.msa_exam.auth.framework.web.dto.UserAuthenticateInputDTO;
import com.sparta.msa_exam.auth.framework.web.dto.UserAuthenticateOutputDTO;
import com.sparta.msa_exam.auth.framework.web.dto.UserCreateInputDTO;
import com.sparta.msa_exam.auth.framework.web.dto.UserCreateOutputDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
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
    private final JwtTokenProvider jwtTokenProvider;

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

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/sign-in")
    public ResponseEntity<UserAuthenticateOutputDTO> authenticateUser(
        @Valid @RequestBody UserAuthenticateInputDTO userAuthenticateInputDTO
    ) {

        UserForAuthenticate userForAuthenticate = UserAuthenticateInputDTO.toDomain(
            userAuthenticateInputDTO
        );
        User user = userUseCase.authenticateUser(userForAuthenticate);

        TokenDTO tokenDTO = jwtTokenProvider.issueTokens(user);

        ResponseCookie refreshCookie = jwtTokenProvider.createRefreshTokenCookie(tokenDTO.refresh());

        return ResponseEntity.ok()
            .header(JwtTokenProvider.HEADER, tokenDTO.access())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(UserAuthenticateOutputDTO.toDTO(user));
    }
}
