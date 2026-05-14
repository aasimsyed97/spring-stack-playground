package com.aasimsyed97.dev_spring_security.model;




public enum RoleType {
    // Decision Point 12: Role naming convention
    ROLE_USER,
    ROLE_MODERATOR,
    ROLE_ADMIN;

    // Decision Point 13: Role hierarchy helper
    public boolean includes(RoleType other) {
        if (this == ROLE_ADMIN) return true;
        if (this == ROLE_MODERATOR) return other == ROLE_USER;
        return this == other;
    }
}
