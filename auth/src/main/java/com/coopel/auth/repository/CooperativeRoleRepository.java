package com.coopel.auth.repository;

import com.coopel.auth.model.CooperativeRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CooperativeRoleRepository extends JpaRepository<CooperativeRole, Integer> {

    @Query("" +
            " from CooperativeRole cr" +
            " where cr.archived is not true and" +
            "   cr.cooperative.remoteId = :remoteId and" +
            "   cr.role.name = :name"
    )
    Optional<CooperativeRole> findByCooperativeRemoteIdAndRoleName(@Param("remoteId") int cooperativeRemoteId, @Param("name") String roleName);
}
