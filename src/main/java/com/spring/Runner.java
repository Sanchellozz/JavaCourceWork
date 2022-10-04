package com.spring;

import com.spring.repository.RoleRepository;
import com.spring.repository.UserRepository;
import com.spring.model.Role;
import com.spring.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class Runner implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {

        if( roleRepository.findAll().isEmpty() ) {

            Role role_user = new Role();
            role_user.setName("USER");
            roleRepository.save(role_user);

            Role role_admin = new Role();
            role_admin.setName("ADMIN");
            roleRepository.save(role_admin);

            Role role_producer = new Role();
            role_producer.setName("PRODUCER");
            roleRepository.save(role_producer);

            User admin = new User();
            admin.setUsername("admin");
            admin.setEnabled(true);
            admin.setPassword(new BCryptPasswordEncoder().encode("admin"));
            admin.setFirstName("Super");
            admin.setLastName("Admin");
            admin.setEmail("admin@alexleo.com");
            admin.setProfilePicPath("/images/default.jpg");

            Role role = roleRepository.findByName("ADMIN"); // admin role fetched
            Set<Role> roles = new HashSet<Role>();
            roles.add(role);
            admin.setRoles(roles);
            userRepository.save(admin);

        }

    }
}
