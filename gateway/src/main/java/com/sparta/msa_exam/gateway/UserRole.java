package com.sparta.msa_exam.gateway;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserRole {

    CUSTOMER("고객"),
    OWNER("점주");

    private final String message;
}
