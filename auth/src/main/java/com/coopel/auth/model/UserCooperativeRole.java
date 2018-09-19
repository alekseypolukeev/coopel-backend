package com.coopel.auth.model;

import com.coopel.jpa.model.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Audited
@Table(uniqueConstraints = @UniqueConstraint(
        columnNames = {"user_id", "cooperative_role_id"}
))
public class UserCooperativeRole extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false, name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false, name = "cooperative_role_id")
    private CooperativeRole cooperativeRole;

}
