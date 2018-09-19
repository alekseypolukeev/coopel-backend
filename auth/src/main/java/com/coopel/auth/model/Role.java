package com.coopel.auth.model;

import com.coopel.jpa.model.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Audited
@Table(name = "auth_role")
public class Role extends AbstractEntity {

    @Column(nullable = false, length = 63, unique = true)
    private String name;

    @OneToMany(mappedBy = "role")
    private Set<CooperativeRole> cooperativeRoles = new HashSet<>();

}
