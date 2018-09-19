package com.coopel.auth.model;

import com.coopel.jpa.model.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Audited
@Table(uniqueConstraints = @UniqueConstraint(
        columnNames = {"cooperative_id", "role_id"}
))
public class CooperativeRole extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false, name = "cooperative_id")
    private Cooperative cooperative;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false, name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "cooperativeRole")
    private Set<UserCooperativeRole> userCooperativeRoles = new HashSet<>();

}
