package com.sparta.msa_exam.auth.application.domain;

public record UserForAuthenticate(
    String username,
    String password
) {

}
