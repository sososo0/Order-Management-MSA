package com.sparta.msa_exam.gateway;

public record UserResponseDTO(
    Long userId,
    String username,
    UserRole userRole
) {

}
