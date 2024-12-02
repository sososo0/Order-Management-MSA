package com.sparta.msa_exam.auth.application.outputport;

import com.sparta.msa_exam.auth.application.domain.User;
import com.sparta.msa_exam.auth.application.domain.UserForCreate;
import java.util.Optional;

public interface UserOutputPort {
    User createUser(UserForCreate userForCreate, String encodedPassword);
    Optional<User> findByUsername(String username);
}
