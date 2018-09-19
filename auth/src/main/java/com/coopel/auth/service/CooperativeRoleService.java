package com.coopel.auth.service;

import com.coopel.auth.model.Cooperative;
import com.coopel.auth.model.CooperativeRole;
import com.coopel.auth.model.Role;
import com.coopel.auth.repository.CooperativeRoleRepository;
import com.coopel.common.exception.EntityNotFoundServiceException;
import com.coopel.jpa.service.bl.AbstractService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class CooperativeRoleService extends AbstractService<CooperativeRole> {

    private final CooperativeRoleRepository cooperativeRoleRepository;
    private final RoleService roleService;
    private final CooperativeService cooperativeService;

    @Inject
    public CooperativeRoleService(CooperativeRoleRepository cooperativeRoleRepository, RoleService roleService, CooperativeService cooperativeService) {
        this.cooperativeRoleRepository = cooperativeRoleRepository;
        this.roleService = roleService;
        this.cooperativeService = cooperativeService;
    }

    public CooperativeRole findByCooperativeRemoteIdAndRoleName(int cooperativeRemoteId, String roleName) {
        return cooperativeRoleRepository.findByCooperativeRemoteIdAndRoleName(cooperativeRemoteId, roleName)
                .orElseThrow(() -> new EntityNotFoundServiceException("CooperativeRole: " + roleName + "_" + cooperativeRemoteId));
    }

    @Override
    protected CooperativeRole executeCreate(CooperativeRole newEntity) {
        Cooperative cooperative = newEntity.getCooperative();
        if (cooperative != null) {
            Integer cooperativeId = cooperative.getId();
            if (cooperativeId != null) {
                Cooperative touchedCooperative = cooperativeService.findById(cooperativeId, true);
                newEntity.setCooperative(touchedCooperative);
            }
        }

        Role role = newEntity.getRole();
        if (role != null) {
            Integer roleId = role.getId();
            if (roleId != null) {
                Role touchedRole = roleService.findById(roleId, true);
                newEntity.setRole(touchedRole);
            }
        }

        return cooperativeRoleRepository.save(newEntity);
    }

    @Override
    protected void executeUpdate(CooperativeRole merged, CooperativeRole update) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected JpaRepository<CooperativeRole, Integer> getRepository() {
        return cooperativeRoleRepository;
    }
}
