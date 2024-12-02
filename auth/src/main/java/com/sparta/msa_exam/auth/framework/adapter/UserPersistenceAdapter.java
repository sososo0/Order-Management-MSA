package com.sparta.msa_exam.auth.framework.adapter;

import com.sparta.msa_exam.auth.application.domain.User;
import com.sparta.msa_exam.auth.application.domain.UserForCreate;
import com.sparta.msa_exam.auth.application.outputport.UserOutputPort;
import com.sparta.msa_exam.auth.domain.model.UserEntity;
import com.sparta.msa_exam.auth.framework.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class UserPersistenceAdapter implements UserOutputPort {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public User createUser(UserForCreate userForCreate, String encodedPassword) {

        UserEntity userEntity = UserEntity.toEntity(
            userForCreate.username(),
            encodedPassword,
            userForCreate.userRole()
        );

        userRepository.save(userEntity);
        userEntity.setAuditBy(userEntity.getId());

        return userEntity.toDomain();
    }
}
