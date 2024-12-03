package com.sparta.msa_exam.auth.application.inputport;

import com.sparta.common.exception.CustomException;
import com.sparta.common.exception.ErrorCode;
import com.sparta.msa_exam.auth.application.domain.User;
import com.sparta.msa_exam.auth.application.domain.UserForAuthenticate;
import com.sparta.msa_exam.auth.application.domain.UserForCreate;
import com.sparta.msa_exam.auth.application.outputport.UserOutputPort;
import com.sparta.msa_exam.auth.application.usecase.UserUseCase;
import java.util.Optional;
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

        checkUsernameDuplication(userForCreate.username());

        String encodedPassword = passwordEncoder.encode(userForCreate.password());

        return userOutputPort.createUser(userForCreate, encodedPassword);
    }

    @Override
    public User authenticateUser(UserForAuthenticate userForAuthenticate) {

        User user = validUsername(userForAuthenticate.username());

        if (!passwordEncoder.matches(userForAuthenticate.password(), user.getPassword())) {
            throw new CustomException(
                ErrorCode.WRONG_PASSWORD.getCode(),
                ErrorCode.WRONG_PASSWORD.getDescription(),
                ErrorCode.WRONG_PASSWORD.getDetailMessage()
            );
        }

        return user;
    }

    private void checkUsernameDuplication(String username) {
        Optional<User> user = userOutputPort.findByUsername(username);
        if (user.isPresent()) {
            throw new CustomException(
                ErrorCode.USERNAME_ALREADY_EXIST.getCode(),
                ErrorCode.USERNAME_ALREADY_EXIST.getDescription(),
                ErrorCode.USERNAME_ALREADY_EXIST.getDetailMessage()
            );
        }
    }

    private User validUsername(String username) {
        User user = userOutputPort.findByUsername(username)
            .orElseThrow(() -> new CustomException(
                ErrorCode.USER_NOT_EXIST.getCode(),
                ErrorCode.USER_NOT_EXIST.getDescription(),
                ErrorCode.USER_NOT_EXIST.getDetailMessage()
            ));
        return user;
    }
}
