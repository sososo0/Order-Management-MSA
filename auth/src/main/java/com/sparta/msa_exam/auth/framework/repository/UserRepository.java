package com.sparta.msa_exam.auth.framework.repository;

import com.sparta.msa_exam.auth.domain.model.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("select u from UserEntity u where u.username = :username")
    Optional<UserEntity> findByUsername(String username);

    @Query("select u from UserEntity u where u.id = :userId")
    Optional<UserEntity> findByUserId(Long userId);
}
