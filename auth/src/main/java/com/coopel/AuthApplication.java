package com.coopel;

import com.coopel.auth.model.Cooperative;
import com.coopel.auth.model.CooperativeRole;
import com.coopel.auth.model.Role;
import com.coopel.auth.repository.*;
import com.coopel.common.role.RoleType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

    // TODo rework with migration microservice
    @Bean
    public CommandLineRunner commandLineRunner(PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository, UserRoleRepository userRoleRepository, CooperativeRepository cooperativeRepository, CooperativeRoleRepository cooperativeRoleRepository, UserCooperativeRoleRepository userCooperativeRoleRepository) {
        return new CommandLineRunner() {

            @Override
            @Transactional
            public void run(String... args) throws Exception {
                try {
                    Role r2 = new Role();
                    r2.setArchived(false);
                    r2.setName(RoleType.User.getName());
                    r2 = roleRepository.save(r2);

                    Role r3 = new Role();
                    r3.setArchived(false);
                    r3.setName(RoleType.Admin.getName());
                    r3 = roleRepository.save(r3);

                    Role r4 = new Role();
                    r4.setArchived(false);
                    r4.setName(RoleType.CoopUser.getName());
                    r4 = roleRepository.save(r4);

                    Cooperative c1 = new Cooperative();
                    c1.setArchived(false);
                    c1.setRemoteId(1000);
                    c1 = cooperativeRepository.save(c1);

                    CooperativeRole cr1 = new CooperativeRole();
                    cr1.setArchived(false);
                    cr1.setCooperative(c1);
                    cr1.setRole(r4);
                    cr1 = cooperativeRoleRepository.save(cr1);

                } catch (Exception e) {
                    //design decision
                }
            }
        };


    }
}
