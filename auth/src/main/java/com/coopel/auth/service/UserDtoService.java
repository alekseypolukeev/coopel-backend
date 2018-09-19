package com.coopel.auth.service;

import com.coopel.auth.dto.CreateUserDto;
import com.coopel.auth.dto.UserDto;
import com.coopel.auth.model.*;
import com.coopel.common.exception.ActionForbiddenServiceException;
import com.coopel.common.exception.BadRequestServiceException;
import com.coopel.common.exception.EntityNotFoundServiceException;
import com.coopel.common.role.RoleType;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserDtoService {

    private static final Logger log = LoggerFactory.getLogger(UserDtoService.class);

    private final UserService userService;
    private final AuthEmailService authEmailService;

    @Inject
    public UserDtoService(UserService userService, AuthEmailService authEmailService) {
        this.userService = userService;
        this.authEmailService = authEmailService;
    }

    public void sendPasswordRecovery(String email) {
        UserDto userDto = findByEmail(email).orElseThrow(() ->
                new BadRequestServiceException("User not found by email: " + email)
        );
        try {
            authEmailService.sendPasswordRecoveryEmail(userDto);
        } catch (Exception e) {
            String message = String.format("Unable to send password email to user: %s", userDto.getId());
            log.error(message, e);
        }
    }

    public void changePassword(int userId, String newPassword, long tokenIssuedAt) {
        userService.updatePassword(userId, newPassword, tokenIssuedAt);
        // TODO send notification email
    }

    public void revokeTokens(int userId) {
        userService.revokeTokens(userId);
        // TODO send notification email
    }

    public void confirmEmail(int userId, long tokenIssuedAt) {
        userService.confirmEmail(userId, tokenIssuedAt);
        // TODO send notification email
    }

    public void sendEmailConfirmation(int userId) {
        UserDto userDto = getUserDto(userId);
        sendInvitationEmail(userDto, false);
    }

    private UserDto getUserDto(int userId) {
        return findById(userId).orElseThrow(() ->
                new EntityNotFoundServiceException("User not found by id: " + userId)
        );
    }

    public UserDto createUser(CreateUserDto user) {
        User newUser = userService.create(toEntity(user));
        UserDto userDto = toDto(newUser);
        sendInvitationEmail(userDto, true);
        return userDto;
    }

    private void sendInvitationEmail(UserDto userDto, boolean firstTime) {
        try {
            authEmailService.sendInvitationEmail(userDto, firstTime);
        } catch (Exception e) {
            String message = String.format("Unable to send invitation email to user: %s", userDto.getId());
            log.error(message, e);
        }
    }

    public UserDto updateUser(int userId, UserDto user) {
        UserDto result = toDto(userService.update(userId, toEntity(user), (merged, update) -> {
            // forbid change of roles
            Set<UserService.UserRoleView> mergedRV = userService.getUserRoleViews(merged);
            Set<UserService.UserRoleView> updateRV = userService.getUserRoleViews(update);
            if (!Objects.equals(mergedRV, updateRV)) {
                throw new ActionForbiddenServiceException("Roles can't be changed");
            }
            Set<UserService.UserCooperativeRoleView> mergedCRV = userService.getUserCooperativeRoleViews(merged);
            Set<UserService.UserCooperativeRoleView> updateCRV = userService.getUserCooperativeRoleViews(update);
            if (!Objects.equals(mergedCRV, updateCRV)) {
                throw new ActionForbiddenServiceException("Cooperative roles can't be changed");
            }
        }));
        if (!Objects.equals(user.getEmail(), result.getEmail())) {
            // TODO send notification email
        }
        return result;
    }

    public Optional<UserDto> findById(int userId) {
        Optional<User> user = userService.findByIdHideArchived(userId);
        return user.map(this::toDto);
    }

    public Optional<UserDto> findByEmail(String email) {
        Optional<User> user = userService.findByEmailHideArchived(email);
        return user.map(this::toDto);
    }

    public Optional<UserDto> findByPhone(String phone) {
        Optional<User> user = userService.findByPhoneHideArchived(phone);
        return user.map(this::toDto);
    }

    private static User toEntity(UserDto d) {
        User e = new User();
        e.setId(d.getId());
        e.setVersion(d.getVersion());
        e.setEmail(d.getEmail());
        e.setPhone(d.getPhone());
        e.setFirstName(d.getFirstName());
        e.setMiddleName(d.getMiddleName());
        e.setLastName(d.getLastName());

        if (d instanceof CreateUserDto) {
            e.setPassword(((CreateUserDto) d).getPassword());
        }

        Set<String> authorities = d.getAuthorities();
        if (authorities != null) {
            for (String authority : authorities) {
                RoleType roleType = RoleType.parse(authority);

                Role role = new Role();
                role.setName(roleType.getName());

                if (roleType.isCoopRole()) {
                    Cooperative cooperative = new Cooperative();
                    cooperative.setRemoteId(roleType.getCoopRemoteId(authority));

                    CooperativeRole cooperativeRole = new CooperativeRole();
                    cooperativeRole.setCooperative(cooperative);
                    cooperativeRole.setRole(role);

                    UserCooperativeRole userCooperativeRole = new UserCooperativeRole();
                    userCooperativeRole.setCooperativeRole(cooperativeRole);

                    e.getUserCooperativeRoles().add(userCooperativeRole);
                } else {
                    UserRole userRole = new UserRole();
                    userRole.setRole(role);

                    e.getUserRoles().add(userRole);
                }
            }
        }

        return e;
    }

    public UserDto toDto(User e) {
        return new UserDto(
                e.getId(),
                e.getVersion(),
                e.getFirstName(),
                e.getMiddleName(),
                e.getLastName(),
                e.getEmail(),
                e.getPhone(),
                buildAuthorities(e)
        );
    }

    public Set<String> buildAuthorities(User e) {
        return Sets.newHashSet(
                Sets.union(
                        e.getUserRoles().stream()
                                .map(userRole -> userRole.getRole().getName())
                                .collect(Collectors.toSet()),

                        e.getUserCooperativeRoles().stream()
                                .map(userCooperativeRole -> {
                                    CooperativeRole cr = userCooperativeRole.getCooperativeRole();
                                    return cr.getRole().getName() + "_" + cr.getCooperative().getRemoteId();
                                }).collect(Collectors.toSet())
                )
        );
    }

}
