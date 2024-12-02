package com.sparta.msa_exam.auth.domain.model;

import com.sparta.common.BaseEntity;
import com.sparta.msa_exam.auth.application.domain.User;
import com.sparta.msa_exam.auth.domain.model.vo.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_user")
public class UserEntity extends BaseEntity {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, columnDefinition = "varchar(255)")
    private String username;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255)")
    private UserRole userRole;

    public void setAuditBy(Long userId) {
        super.createFrom(userId);
        super.updateFrom(userId);
    }

    private UserEntity(
        String username,
        String encodedPassword,
        UserRole userRole
    ) {
        this.username = username;
        this.password = encodedPassword;
        this.userRole = userRole;
    }

    public static UserEntity toEntity(
        String username,
        String encodedPassword,
        UserRole userRole
    ) {
        return new UserEntity(
            username,
            encodedPassword,
            userRole
        );
    }

    public User toDomain() {
        return new User(
            this.id,
            this.username,
            this.userRole
        );
    }
}
