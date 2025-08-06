package com.chirag.doctorappointmentsystem.service;

import com.chirag.doctorappointmentsystem.model.User;
import com.chirag.doctorappointmentsystem.model.Appointment;
import com.chirag.doctorappointmentsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentService appointmentService; // Add this line

    @Override
    public User registerUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public User validateUser(String email, String password) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    @Override
    public User updateUser(User user) {
        // Save will update if ID exists
        return userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            // Delete all appointments where this user is a patient or doctor
            List<Appointment> allAppointments = appointmentService.getAllAppointments();
            for (Appointment app : allAppointments) {
                if ((app.getPatient() != null && app.getPatient().getId().equals(id)) ||
                    (app.getDoctor() != null && app.getDoctor().getId().equals(id))) {
                    appointmentService.deleteAppointment(app.getId());
                }
            }
            // Now delete the user
            userRepository.deleteById(id);
        }
    }
}
