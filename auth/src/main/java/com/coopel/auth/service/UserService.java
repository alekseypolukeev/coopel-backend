package com.coopel.auth.service;

import com.coopel.auth.helper.EmailHelper;
import com.coopel.auth.helper.PasswordHelper;
import com.coopel.auth.helper.PhoneHelper;
import com.coopel.auth.model.Role;
import com.coopel.auth.model.User;
import com.coopel.auth.model.UserCooperativeRole;
import com.coopel.auth.model.UserRole;
import com.coopel.auth.repository.UserRepository;
import com.coopel.common.exception.RevokedTokenServiceException;
import com.coopel.common.helper.CollectionUtils;
import com.coopel.common.role.RoleType;
import com.coopel.jpa.service.bl.AbstractService;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserService extends AbstractService<User> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleService userRoleService;
    private final UserCooperativeRoleService userCooperativeRoleService;

    @Inject
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserRoleService userRoleService, UserCooperativeRoleService userCooperativeRoleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRoleService = userRoleService;
        this.userCooperativeRoleService = userCooperativeRoleService;
    }

    public Optional<User> findByIdHideArchived(int id) {
        return userRepository.findOneByIdAndArchivedNotTrue(id);
    }

    public Optional<User> findByEmailHideArchived(String email) {
        return userRepository.findOneByEmailAndArchivedNotTrue(EmailHelper.adjust(email));
    }

    public Optional<User> findByPhoneHideArchived(String phone) {
        return userRepository.findOneByPhoneAndArchivedNotTrue(PhoneHelper.adjust(phone));
    }

    @Override
    protected User executeCreate(User newEntity) {

        Set<UserRole> userRoles = newEntity.getUserRoles();
        newEntity.setUserRoles(Collections.emptySet());

        Set<UserCooperativeRole> userCooperativeRoles = newEntity.getUserCooperativeRoles();
        newEntity.setUserCooperativeRoles(Collections.emptySet());

        newEntity.setEmail(EmailHelper.adjust(newEntity.getEmail()));
        newEntity.setPhone(PhoneHelper.adjust(newEntity.getPhone()));

        String password = PasswordHelper.adjust(newEntity.getPassword());
        newEntity.setPassword(passwordEncoder.encode(password));

        User user = userRepository.save(newEntity);

        user.setUserRoles(userRoles.stream()
                .map(userRole -> {
                    userRole.setUser(user);
                    return userRoleService.create(userRole);
                }).collect(Collectors.toSet())
        );

        user.setUserCooperativeRoles(userCooperativeRoles.stream()
                .map(userCooperativeRole -> {
                    userCooperativeRole.setUser(user);
                    return userCooperativeRoleService.create(userCooperativeRole);
                }).collect(Collectors.toSet())
        );

        return user;
    }

    @Transactional
    public void confirmEmail(int userId, long tokenIssuedAt) {
        User merged = findById(userId, true);

        long notBefore = Math.max(
                CollectionUtils.nullToZero(merged.getTokensNotBefore()),
                CollectionUtils.nullToZero(merged.getEmailConfirmTokensNotBefore())
        );
        if (tokenIssuedAt < notBefore) {
            throw new RevokedTokenServiceException("token has been revoked");
        }

        // add User role to confirm email
        Role role = new Role();
        role.setName(RoleType.User.getName());

        UserRole userRole = new UserRole();
        userRole.setUser(merged);
        userRole.setRole(role);

        userRoleService.create(userRole);

        revokeEmailConfirmTokens(merged);
    }

    @Transactional
    public void updatePassword(int userId, String newPassword, long tokenIssuedAt) {
        PasswordHelper.check(newPassword);
        User merged = findById(userId, true);

        long notBefore = CollectionUtils.nullToZero(merged.getTokensNotBefore());
        if (tokenIssuedAt < notBefore) {
            throw new RevokedTokenServiceException("token has been revoked");
        }

        merged.setPassword(passwordEncoder.encode(PasswordHelper.adjust(newPassword)));
        revokeTokens(merged);
    }

    @Transactional
    public void revokeTokens(int userId) {
        User merged = findById(userId, true);
        revokeTokens(merged);
    }

    @Override
    protected void executeUpdate(User merged, User update) {
        String oldEmail = merged.getEmail();
        String newEmail = EmailHelper.adjust(update.getEmail());
        if (!Objects.equals(oldEmail, newEmail)) {
            // remove User role if email has been changed
            Set<UserRole> userRoles = update.getUserRoles();
            if (userRoles != null) {
                update.setUserRoles(userRoles.stream()
                        .filter(role -> {
                            String roleName = role.getRole().getName();
                            return RoleType.parse(roleName) != RoleType.User;
                        }).collect(Collectors.toSet())
                );
            }
            revokeTokens(merged);
        }
        merged.setEmail(newEmail);
        merged.setPhone(PhoneHelper.adjust(update.getPhone()));

        merged.setFirstName(update.getFirstName());
        merged.setMiddleName(update.getMiddleName());
        merged.setLastName(update.getLastName());

        updateUserRoles(merged, update);
        updateUserCooperativeRoles(merged, update);
    }

    private void updateUserRoles(User merged, User update) {
        Set<UserRoleView> mergedViews = getUserRoleViews(merged);
        Set<UserRoleView> updateViews = getUserRoleViews(update);

        Set<UserRoleView> removeViews = Sets.difference(mergedViews, updateViews);
        for (UserRoleView view : removeViews) {
            userRoleService.delete(view.src.getId());
            merged.getUserRoles().remove(view.src);
        }

        Sets.SetView<UserRoleView> addViews = Sets.difference(updateViews, mergedViews);
        for (UserRoleView view : addViews) {
            view.src.setUser(merged);
            userRoleService.create(view.src);
            merged.getUserRoles().add(view.src);
        }
    }

    Set<UserRoleView> getUserRoleViews(User user) {
        return user.getUserRoles().stream()
                .map(UserRoleView::new)
                .collect(Collectors.toSet());
    }

    private void updateUserCooperativeRoles(User merged, User update) {
        Set<UserCooperativeRoleView> mergedViews = getUserCooperativeRoleViews(merged);
        Set<UserCooperativeRoleView> updateViews = getUserCooperativeRoleViews(update);

        Set<UserCooperativeRoleView> removeViews = Sets.difference(mergedViews, updateViews);
        for (UserCooperativeRoleView view : removeViews) {
            userCooperativeRoleService.delete(view.src.getId());
            merged.getUserCooperativeRoles().remove(view.src);
        }

        Sets.SetView<UserCooperativeRoleView> addViews = Sets.difference(updateViews, mergedViews);
        for (UserCooperativeRoleView view : addViews) {
            view.src.setUser(merged);
            userCooperativeRoleService.create(view.src);
            merged.getUserCooperativeRoles().add(view.src);
        }
    }

    Set<UserCooperativeRoleView> getUserCooperativeRoleViews(User user) {
        return user.getUserCooperativeRoles().stream()
                .map(UserCooperativeRoleView::new)
                .collect(Collectors.toSet());
    }

    @Override
    protected void executeValidate(User merged, User update, boolean create) {
        EmailHelper.check(update.getEmail());
        PhoneHelper.check(update.getPhone());
        PasswordHelper.check(update.getPassword());
    }


    @Override
    protected JpaRepository<User, Integer> getRepository() {
        return userRepository;
    }

    private static void revokeEmailConfirmTokens(User merged) {
        merged.setEmailConfirmTokensNotBefore(Instant.now().getEpochSecond());
    }

    private static void revokeTokens(User merged) {
        merged.setTokensNotBefore(Instant.now().getEpochSecond());
    }

    // Helper classes --------------------------------------------------------------------------------------------------
    @AllArgsConstructor
    static class UserRoleView {

        private final UserRole src;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserRoleView that = (UserRoleView) o;
            return Objects.equals(src.getRole().getName(), that.src.getRole().getName());
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    src.getRole().getName()
            );
        }
    }

    @AllArgsConstructor
    static class UserCooperativeRoleView {

        private final UserCooperativeRole src;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserCooperativeRoleView that = (UserCooperativeRoleView) o;
            return Objects.equals(src.getCooperativeRole().getCooperative().getRemoteId(), that.src.getCooperativeRole().getCooperative().getRemoteId()) &&
                    Objects.equals(src.getCooperativeRole().getRole().getName(), that.src.getCooperativeRole().getRole().getName());
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    src.getCooperativeRole().getCooperative().getRemoteId(),
                    src.getCooperativeRole().getRole().getName()
            );
        }
    }

}
