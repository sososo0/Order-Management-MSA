package com.sparta.msa_exam.auth.framework.adapter;

import com.sparta.msa_exam.auth.application.domain.User;
import com.sparta.msa_exam.auth.application.domain.UserForCreate;
import com.sparta.msa_exam.auth.application.outputport.UserOutputPort;
import com.sparta.msa_exam.auth.domain.model.UserEntity;
import com.sparta.msa_exam.auth.framework.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class UserPersistenceAdapter implements UserOutputPort {

    private final UserRepository userRepository;

    public Optional<User> findByUserId(Long userId) {
        return userRepository.findByUserId(userId)
            .map(UserEntity::toDomain)
            .or(Optional::empty);
    }

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

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username)
            .map(UserEntity::toDomain)
            .or(Optional::empty);
    }
}
