package com.coopel.auth.service;

import com.coopel.auth.model.Role;
import com.coopel.auth.repository.RoleRepository;
import com.coopel.common.exception.EntityNotFoundServiceException;
import com.coopel.jpa.service.bl.AbstractService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class RoleService extends AbstractService<Role> {

    private final RoleRepository roleRepository;

    @Inject
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role findByName(String name) {
        return roleRepository.findOneByName(name)
                .orElseThrow(() -> new EntityNotFoundServiceException("role: " + name));
    }

    @Override
    protected Role executeCreate(Role newEntity) {
        return roleRepository.save(newEntity);
    }

    @Override
    protected void executeUpdate(Role merged, Role update) {
        merged.setName(update.getName());
    }

    @Override
    protected JpaRepository<Role, Integer> getRepository() {
        return roleRepository;
    }
}
