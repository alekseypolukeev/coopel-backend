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
@Table(name = "auth_user")
public class User extends AbstractEntity {

    @Column(nullable = false, length = 31)
    private String firstName;

    @Column(length = 31)
    private String middleName;

    @Column(nullable = false, length = 31)
    private String lastName;

    @Column(nullable = false, length = 63, unique = true)
    private String email;

    @Column(length = 15, unique = true)
    private String phone;

    @Column(nullable = false, length = 127)
    private String password;

    @OneToMany(mappedBy = "user")
    private Set<UserRole> userRoles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<UserCooperativeRole> userCooperativeRoles = new HashSet<>();

    private Long tokensNotBefore;

    private Long emailConfirmTokensNotBefore;

}
