package com.sparta.msa_exam.auth.application.outputport;

import com.sparta.msa_exam.auth.application.domain.User;
import com.sparta.msa_exam.auth.application.domain.UserForCreate;

public interface UserOutputPort {
    User createUser(UserForCreate userForCreate, String encodedPassword);
}
