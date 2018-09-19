package com.coopel.auth.model;

import com.coopel.jpa.model.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Audited
public class Cooperative extends AbstractEntity {

    @Column(nullable = false, updatable = false, unique = true)
    private Integer remoteId;

    @OneToMany(mappedBy = "cooperative")
    private Set<CooperativeRole> cooperativeRoles = new HashSet<>();

}
