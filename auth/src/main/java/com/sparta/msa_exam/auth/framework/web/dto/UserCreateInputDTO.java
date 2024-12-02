package com.sparta.msa_exam.auth.framework.web.dto;

import com.sparta.msa_exam.auth.application.domain.UserForCreate;
import com.sparta.msa_exam.auth.domain.model.vo.UserRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserCreateInputDTO(
    @Pattern(
        regexp = "^[a-zA-Z0-9]{4,10}$",
        message = "이름은 4자 이상 10자 이하의 알파벳 대소문자와 숫자로 구성되어야 합니다."
    )
    String username,

    @Pattern(
        regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+~`|}{\\[\\]:;?><,./-₩]).{8,15}$",
        message = "비밀번호는 8자 이상 15자 이하의 알파벳, 숫자, 특수문자를 포함해야 합니다."
    )
    String password,

    @NotNull
    UserRole userRole
) {
    public static UserForCreate toDomain(
        UserCreateInputDTO userCreateInputDTO
    ) {
        return new UserForCreate(
            userCreateInputDTO.username,
            userCreateInputDTO.password,
            userCreateInputDTO.userRole
        );
    }
}
