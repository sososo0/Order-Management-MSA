package com.sparta.msa_exam.auth.application.inputport;

import com.sparta.msa_exam.auth.application.domain.User;
import com.sparta.msa_exam.auth.application.domain.UserForAuthenticate;
import com.sparta.msa_exam.auth.application.domain.UserForCreate;
import com.sparta.msa_exam.auth.application.outputport.UserOutputPort;
import com.sparta.msa_exam.auth.application.usecase.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInputPort implements UserUseCase {

    private final PasswordEncoder passwordEncoder;
    private final UserOutputPort userOutputPort;

    @Override
    public User createUser(UserForCreate userForCreate) {

        // TODO : 사용자 예외 처리(ex. 중복 처리 등등)

        String encodedPassword = passwordEncoder.encode(userForCreate.password());

        return userOutputPort.createUser(userForCreate, encodedPassword);
    }

    @Override
    public User authenticateUser(UserForAuthenticate userForAuthenticate) {

        // TODO : 존재하지 않는 ID
        User user = userOutputPort.findByUsername(userForAuthenticate.username())
            .orElseThrow();

        // TODO : 잘못된 비밀번호
        if (!passwordEncoder.matches(userForAuthenticate.password(), user.getPassword())) {
            // TODO : 예외 처리
        }

        return user;
    }
}