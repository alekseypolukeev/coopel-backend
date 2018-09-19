package com.coopel.common.role;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum RoleType {

    User("R_U"),
    Admin("R_A"),

    CoopUser("CR_U", true);

    private static List<RoleType> ALL = Arrays.asList(RoleType.values());

    private final String name;
    private final boolean isCoopRole;

    RoleType(String name) {
        this(name, false);
    }

    RoleType(String name, boolean isCoopRole) {
        this.name = name;
        this.isCoopRole = isCoopRole;
    }

    public int getCoopRemoteId(String name) {
        if (isCoopRole) {
            return Integer.parseInt(name.substring(this.name.length() + 1));
        }
        throw new UnsupportedOperationException();
    }

    public static RoleType parse(String name) {
        name = name.trim();
        for (RoleType role : ALL) {
            if (role.isCoopRole && name.startsWith(role.name) || !role.isCoopRole && name.equals(role.name)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unsupported role: " + name);
    }

}
