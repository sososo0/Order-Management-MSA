package com.sparta.msa_exam.auth.domain.model.vo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserRole {

    CUSTOMER("고객"),
    OWNER("점주");

    private final String message;
}
