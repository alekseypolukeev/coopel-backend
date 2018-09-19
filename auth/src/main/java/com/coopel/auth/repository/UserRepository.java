package com.coopel.auth.repository;

import com.coopel.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("" +
            " select u" +
            " from User u" +
            "   left join fetch u.userRoles ur" +
            "   left join fetch ur.role" +
            "   left join fetch u.userCooperativeRoles ucr" +
            "   left join fetch ucr.cooperativeRole cr" +
            "   left join fetch cr.cooperative " +
            "   left join fetch cr.role " +
            " where u.archived is not true and u.id = :id"
    )
    Optional<User> findOneByIdAndArchivedNotTrue(@Param("id") int id);

    @Query("" +
            " select u" +
            " from User u" +
            "   left join fetch u.userRoles ur" +
            "   left join fetch ur.role" +
            "   left join fetch u.userCooperativeRoles ucr" +
            "   left join fetch ucr.cooperativeRole cr" +
            "   left join fetch cr.cooperative " +
            "   left join fetch cr.role " +
            " where u.archived is not true and u.email = :email"
    )
    Optional<User> findOneByEmailAndArchivedNotTrue(@Param("email") String email);

    @Query("" +
            " select u" +
            " from User u" +
            "   left join fetch u.userRoles ur" +
            "   left join fetch ur.role" +
            "   left join fetch u.userCooperativeRoles ucr" +
            "   left join fetch ucr.cooperativeRole cr" +
            "   left join fetch cr.cooperative " +
            "   left join fetch cr.role " +
            " where u.archived is not true and u.phone = :phone"
    )
    Optional<User> findOneByPhoneAndArchivedNotTrue(@Param("phone") String phone);

}
