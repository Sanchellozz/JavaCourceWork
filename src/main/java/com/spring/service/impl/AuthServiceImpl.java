package com.spring.service.impl;

import com.spring.repository.RoleRepository;
import com.spring.repository.UserRepository;
import com.spring.exception.ResourceAlreadyExists;
import com.spring.exception.ResourceNotFoundException;
import com.spring.model.Role;
import com.spring.model.User;
import com.spring.security.MyUserDetails;
import com.spring.service.AuthService;
import com.spring.utility.FileUpload;
import com.spring.utility.constants.ImageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void signupSubmit(User user) throws ResourceAlreadyExists {

        if (userRepository.findByUsername(user.getUsername()) != null)
            throw new ResourceAlreadyExists("Username Already exists");


        Role role = roleRepository.findByName("USER");
        Set<Role> roles = new HashSet<Role>();
        roles.add(role);
        user.setRoles(roles);
        user.setProfilePicPath("/images/default.jpg");
        user.setEnabled(true);
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public List<User> allUser() throws ResourceNotFoundException {
        List<User> users = userRepository.findAllUser();
        if (users.isEmpty())
            throw new ResourceNotFoundException("No User found");
        return users;
    }

    @Override
    public void deleteUser(Long userId) throws ResourceNotFoundException {

        userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userRepository.deleteById(userId);
    }

    @Override
    public User profile(MyUserDetails principal) {
        User user = userRepository.findById(principal.getId()).orElse(new User());
        return user;
    }

    @Override
    public User setEditUserPage(Long userId) throws ResourceNotFoundException {

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return user;
    }

    @Override
    public void editUserSubmit(Long userId, User userDetails) {

        User user = userRepository.findById(userId).orElse(new User());
        user.setUsername(userDetails.getUsername());
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setRoles(userDetails.getRoles());
        userRepository.save(user);
    }

    @Override
    public void saveUpdatedProfilePicture(Long userId, MultipartFile file) throws ResourceNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String path = FileUpload.saveImage(ImageType.USER_PROFILE, user.getUsername(), file);
        user.setProfilePicPath(path);
        userRepository.save(user);
    }

}
