package com.sparta.msa_exam.auth.framework.web.dto;

public record TokenDTO(
    String access,
    String refresh
) {

}
