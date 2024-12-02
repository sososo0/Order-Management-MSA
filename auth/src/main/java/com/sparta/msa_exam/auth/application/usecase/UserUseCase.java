package com.sparta.msa_exam.auth.application.usecase;

import com.sparta.msa_exam.auth.application.domain.User;
import com.sparta.msa_exam.auth.application.domain.UserForCreate;

public interface UserUseCase {
    User createUser(UserForCreate userForCreate);
}
