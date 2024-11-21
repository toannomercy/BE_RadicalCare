package com.radical.be_radicalcare.Constants;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RoleType {
    ADMIN(1),
    USER(2),
    CREATOR(3),
    SUPER_ADMIN(4);
    public final long value;
}