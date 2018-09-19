package com.coopel.auth.service;

import com.coopel.auth.model.Role;
import com.coopel.auth.model.User;
import com.coopel.auth.model.UserRole;
import com.coopel.auth.repository.UserRoleRepository;
import com.coopel.jpa.service.bl.AbstractService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Provider;

@Component
public class UserRoleService extends AbstractService<UserRole> {

    private final UserRoleRepository userRoleRepository;
    private final RoleService roleService;
    private final Provider<UserService> userService;

    @Inject
    public UserRoleService(UserRoleRepository userRoleRepository, RoleService roleService, Provider<UserService> userService) {
        this.userRoleRepository = userRoleRepository;
        this.roleService = roleService;
        this.userService = userService;
    }

    @Override
    protected UserRole executeCreate(UserRole newEntity) {
        User user = newEntity.getUser();
        if (user != null) {
            Integer userId = user.getId();
            if (userId != null) {
                User touchedUser = userService.get().findById(userId, true);
                newEntity.setUser(touchedUser);
            }
        }

        Role role = newEntity.getRole();
        if (role != null) {
            String name = role.getName();
            if (name != null) {
                Role touchedRole = roleService.findByName(name);
                newEntity.setRole(touchedRole);
            }
        }

        return userRoleRepository.save(newEntity);
    }

    @Override
    protected void executeUpdate(UserRole merged, UserRole update) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected JpaRepository<UserRole, Integer> getRepository() {
        return userRoleRepository;
    }
}
