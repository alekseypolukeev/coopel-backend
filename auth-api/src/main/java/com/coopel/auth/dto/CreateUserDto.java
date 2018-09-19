package com.coopel.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateUserDto extends UserDto {

    private String password;

    public CreateUserDto(UserDto s, String password) {
        super(s);
        this.password = password;
    }
}
