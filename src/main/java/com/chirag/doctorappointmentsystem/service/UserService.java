package com.chirag.doctorappointmentsystem.service;

import com.chirag.doctorappointmentsystem.model.User;
import java.util.List;

public interface UserService {
    User registerUser(User user);
    List<User> getAllUsers();
    User getUserById(Long id);
    User getUserByEmail(String email);
    User validateUser(String email, String password);

    // ✅ New methods for admin operations
    User updateUser(User user);
    void deleteUserById(Long id);
}
