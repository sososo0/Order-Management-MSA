package com.sparta.msa_exam.auth.framework.repository;

import com.sparta.msa_exam.auth.domain.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

}
