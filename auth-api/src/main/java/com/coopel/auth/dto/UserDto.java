package com.coopel.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {

    private Integer id;
    private Integer version;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phone;
    private Set<String> authorities;

    public UserDto(Integer id, Integer version, String firstName, String middleName, String lastName, String email, String phone, Set<String> authorities) {
        this.id = id;
        this.version = version;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.authorities = authorities;
    }

    public UserDto(UserDto s) {
        this(
                s.getId(),
                s.getVersion(),
                s.getFirstName(),
                s.getMiddleName(),
                s.getLastName(),
                s.getEmail(),
                s.getPhone(),
                s.getAuthorities()
        );
    }
}
