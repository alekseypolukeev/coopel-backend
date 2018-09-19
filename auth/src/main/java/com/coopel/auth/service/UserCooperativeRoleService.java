package com.coopel.auth.service;

import com.coopel.auth.model.*;
import com.coopel.auth.repository.UserCooperativeRoleRepository;
import com.coopel.jpa.service.bl.AbstractService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Provider;

@Component
public class UserCooperativeRoleService extends AbstractService<UserCooperativeRole> {

    private final UserCooperativeRoleRepository userCooperativeRoleRepository;
    private final Provider<UserService> userService;
    private final CooperativeRoleService cooperativeRoleService;

    @Inject
    public UserCooperativeRoleService(UserCooperativeRoleRepository userCooperativeRoleRepository, Provider<UserService> userService, CooperativeRoleService cooperativeRoleService) {
        this.userCooperativeRoleRepository = userCooperativeRoleRepository;
        this.userService = userService;
        this.cooperativeRoleService = cooperativeRoleService;
    }

    @Override
    protected UserCooperativeRole executeCreate(UserCooperativeRole newEntity) {
        User user = newEntity.getUser();
        if (user != null) {
            Integer userId = user.getId();
            if (userId != null) {
                User touchedUser = userService.get().findById(userId, true);
                newEntity.setUser(touchedUser);
            }
        }

        CooperativeRole cooperativeRole = newEntity.getCooperativeRole();
        if (cooperativeRole != null) {
            Cooperative cooperative = cooperativeRole.getCooperative();
            Role role = cooperativeRole.getRole();
            if (cooperative != null && role != null) {
                Integer cooperativeRemoteId = cooperative.getRemoteId();
                String roleName = role.getName();
                if (cooperativeRemoteId != null && roleName != null) {
                    CooperativeRole touchedCooperativeRole = cooperativeRoleService.findByCooperativeRemoteIdAndRoleName(cooperativeRemoteId, roleName);
                    newEntity.setCooperativeRole(touchedCooperativeRole);
                }
            }
        }

        return userCooperativeRoleRepository.save(newEntity);
    }

    @Override
    protected void executeUpdate(UserCooperativeRole merged, UserCooperativeRole update) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected JpaRepository<UserCooperativeRole, Integer> getRepository() {
        return userCooperativeRoleRepository;
    }
}
