package com.coopel.auth.rest;

import com.coopel.auth.dto.CreateUserDto;
import com.coopel.auth.dto.UserDto;
import com.coopel.auth.service.UserDtoService;
import com.coopel.common.context.IdentityContextHolder;
import com.coopel.common.exception.ActionForbiddenServiceException;
import com.coopel.common.exception.EntityNotFoundServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
public class UserRestClientImpl implements UserRestClient {

    private final IdentityContextHolder identityContextHolder;
    private final UserDtoService userDtoService;

    @Inject
    public UserRestClientImpl(IdentityContextHolder identityContextHolder, UserDtoService userDtoService) {
        this.identityContextHolder = identityContextHolder;
        this.userDtoService = userDtoService;
    }

    @Override
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserDto user) {
        if (user.getAuthorities() != null && !user.getAuthorities().isEmpty()) {
            throw new ActionForbiddenServiceException("authorities forbidden");
        }

        UserDto result = userDtoService.createUser(user);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("#oauth2.hasScope('update') and #common.isUser(#id) and #id == #user.id")
    public ResponseEntity<UserDto> updateUser(@PathVariable int id, @RequestBody UserDto user) {
        UserDto result = userDtoService.updateUser(id, user);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("#oauth2.hasScope('get')")
    public ResponseEntity<UserDto> getMe() {
        int userId = identityContextHolder.getIdentity().getUserId();
        UserDto result = userDtoService.findById(userId)
                .orElseThrow(() -> new EntityNotFoundServiceException("user: " + userId));
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("#oauth2.hasScope('do') and #common.isUser(#id)")
    public void sendEmailConfirmation(@PathVariable int id) {
        userDtoService.sendEmailConfirmation(id);
    }

    @Override
    @PreAuthorize("#oauth2.hasScope('confirmEmail') and #common.isUser(#id)")
    public void confirmEmail(@PathVariable int id) {
        long tokenIssuedAt = identityContextHolder.getIdentity().getTokenIssuedAt();
        userDtoService.confirmEmail(id, tokenIssuedAt);
    }

    @Override
    @PreAuthorize("#oauth2.hasScope('confirmEmail')")
    public void confirmMyEmail() {
        int userId = identityContextHolder.getIdentity().getUserId();
        confirmEmail(userId);
    }

    @Override
    public void sendPasswordRecovery(@RequestBody String email) {
        userDtoService.sendPasswordRecovery(email);
    }

    @Override
    @PreAuthorize("#oauth2.hasScope('recoverPwd') and #common.isUser(#id)")
    public void changePassword(@PathVariable int id, @RequestBody String newPassword) {
        long tokenIssuedAt = identityContextHolder.getIdentity().getTokenIssuedAt();
        userDtoService.changePassword(id, newPassword, tokenIssuedAt);
    }

    @Override
    @PreAuthorize("#oauth2.hasScope('recoverPwd')")
    public void changeMyPassword(@RequestBody String newPassword) {
        int userId = identityContextHolder.getIdentity().getUserId();
        changePassword(userId, newPassword);
    }

    @Override
    @PreAuthorize("#oauth2.hasScope('do') and #common.isUser(#id)")
    public void revokeSessions(@PathVariable int id) {
        userDtoService.revokeTokens(id);
    }

    @Override
    @PreAuthorize("#oauth2.hasScope('do')")
    public void revokeMySessions() {
        int userId = identityContextHolder.getIdentity().getUserId();
        revokeSessions(userId);
    }

}
