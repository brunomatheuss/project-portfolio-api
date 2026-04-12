package com.brunomatheus.portfolio.enums;

import lombok.Getter;

@Getter
public enum MemberRole {

    EMPLOYEE("FUNCIONARIO"),
    OTHER("OUTRO");

    private final String role;

    MemberRole(String role) {
        this.role = role;
    }

}