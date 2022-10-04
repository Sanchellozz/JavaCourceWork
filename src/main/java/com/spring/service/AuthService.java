package com.spring.service;

import com.spring.exception.ResourceAlreadyExists;
import com.spring.exception.ResourceNotFoundException;
import com.spring.model.User;
import com.spring.security.MyUserDetails;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface AuthService {

    void signupSubmit(User user) throws ResourceAlreadyExists;

    List<User> allUser() throws ResourceNotFoundException;

    void deleteUser(Long userId) throws ResourceNotFoundException;

    User profile(MyUserDetails principal);

    User setEditUserPage(Long userId) throws ResourceNotFoundException;

    void editUserSubmit(Long userId, User userDetails);

    void saveUpdatedProfilePicture(Long userId, MultipartFile file) throws ResourceNotFoundException;

}
