package com.coopel.auth.rest;

import com.coopel.auth.dto.CreateUserDto;
import com.coopel.auth.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.coopel.common.rest.PathConstants.PUBLIC;
import static com.coopel.common.rest.PathConstants.V1;

@RequestMapping
public interface UserRestClient {

    @PostMapping(PUBLIC + V1 + "/users")
    ResponseEntity<UserDto> createUser(@RequestBody CreateUserDto user);

    @PutMapping(V1 + "/users/{id}")
    ResponseEntity<UserDto> updateUser(@PathVariable int id, @RequestBody UserDto user);

    @GetMapping(V1 + "/users/me")
    ResponseEntity<UserDto> getMe();

    @PostMapping(V1 + "/users/{id}/send-email-confirmation")
    void sendEmailConfirmation(@PathVariable int id);

    @PostMapping(V1 + "/users/{id}/confirm-email")
    void confirmEmail(@PathVariable int id);

    @PostMapping(V1 + "/users/me/confirm-email")
    void confirmMyEmail();

    @PostMapping(PUBLIC + V1 + "/users/send-password-recovery")
    void sendPasswordRecovery(@RequestBody String email);

    @PostMapping(V1 + "/users/{id}/set-password")
    void changePassword(@PathVariable int id, @RequestBody String newPassword);

    @PostMapping(V1 + "/users/me/set-password")
    void changeMyPassword(@RequestBody String newPassword);

    @PostMapping(V1 + "/users/{id}/revoke-sessions")
    void revokeSessions(@PathVariable int id);

    @PostMapping(V1 + "/users/me/revoke-sessions")
    void revokeMySessions();

}
