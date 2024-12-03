package com.sparta.msa_exam.auth.framework.web.controller;

import com.sparta.msa_exam.auth.application.domain.User;
import com.sparta.msa_exam.auth.framework.adapter.UserPersistenceAdapter;
import com.sparta.msa_exam.auth.framework.web.dto.UserReadOutputDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class UserQueryController {

    private final UserPersistenceAdapter userPersistenceAdapter;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}")
    public UserReadOutputDTO getUser(
        @PathVariable(name = "userId") Long userId
    ) {
        // TODO: 예외처리하기
        User user = userPersistenceAdapter.findByUserId(userId).get();
        return UserReadOutputDTO.toDTO(user);
    }
}
