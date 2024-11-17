package com.radical.be_radicalcare.Constants;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RoleType {
    ADMIN(9),
    USER(10),
    CREATOR(11),
    SUPER_ADMIN(12);
    public final long value;
}