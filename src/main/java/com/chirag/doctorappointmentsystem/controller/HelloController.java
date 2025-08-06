package com.chirag.doctorappointmentsystem.controller;

import com.chirag.doctorappointmentsystem.model.Appointment;
import com.chirag.doctorappointmentsystem.model.Role;
import com.chirag.doctorappointmentsystem.model.User;
import com.chirag.doctorappointmentsystem.service.AppointmentService;
import com.chirag.doctorappointmentsystem.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class HelloController {

    @Autowired
    private UserService userService;

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginError", null);
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password,
                               Model model) {
        User user = userService.validateUser(email, password);
        if (user != null) {
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                return "redirect:/admin";
            } else if ("DOCTOR".equalsIgnoreCase(user.getRole())) {
                return "redirect:/doctor/dashboard?email=" + email;
            } else if ("PATIENT".equalsIgnoreCase(user.getRole())) {
                return "redirect:/patient/dashboard?email=" + email;
            } else {
                return "redirect:/home";
            }
        } else {
            model.addAttribute("loginError", "Invalid email or password");
            return "login";
        }
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", Role.values());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        if (userService.getUserByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "Email already registered");
            model.addAttribute("roles", Role.values());
            return "register";
        }

        if (!"DOCTOR".equalsIgnoreCase(user.getRole())) {
            user.setSpecialization(null);
        }

        userService.registerUser(user);
        return "redirect:/login";
    }

    // ------------------ ADMIN DASHBOARD ------------------

    @GetMapping("/admin")
    public String showAdminDashboard(Model model) {
        List<User> allUsers = userService.getAllUsers();
        List<User> doctors = allUsers.stream()
                .filter(user -> "DOCTOR".equalsIgnoreCase(user.getRole()))
                .toList();
        List<User> patients = allUsers.stream()
                .filter(user -> "PATIENT".equalsIgnoreCase(user.getRole()))
                .toList();
        List<Appointment> allAppointments = appointmentService.getAllAppointments();

        model.addAttribute("users", allUsers);
        model.addAttribute("doctors", doctors);
        model.addAttribute("patients", patients);
        model.addAttribute("newUser", new User());
        model.addAttribute("roles", Role.values());
        model.addAttribute("appointments", allAppointments);
        model.addAttribute("editAppointment", new Appointment()); // blank by default
        return "admin-dashboard";
    }

    @PostMapping("/admin/add")
    public String addUser(@ModelAttribute("newUser") User user) {
        if (!"DOCTOR".equalsIgnoreCase(user.getRole())) {
            user.setSpecialization(null);
        }
        userService.registerUser(user);
        return "redirect:/admin";
    }

    @PostMapping("/admin/update")
    public String updateUser(@ModelAttribute("newUser") User user) {
        if (!"DOCTOR".equalsIgnoreCase(user.getRole())) {
            user.setSpecialization(null);
        }
        userService.updateUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/admin/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin";
    }

    // ------------------ ADMIN APPOINTMENT MANAGEMENT ------------------

    @GetMapping("/admin/appointment/edit/{id}")
    public String showEditAppointmentForm(@PathVariable Long id, Model model) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        List<User> allUsers = userService.getAllUsers();
        List<User> doctors = allUsers.stream()
                .filter(user -> "DOCTOR".equalsIgnoreCase(user.getRole()))
                .toList();
        List<User> patients = allUsers.stream()
                .filter(user -> "PATIENT".equalsIgnoreCase(user.getRole()))
                .toList();
        List<Appointment> allAppointments = appointmentService.getAllAppointments();

        model.addAttribute("editAppointment", appointment);
        model.addAttribute("doctors", doctors);
        model.addAttribute("patients", patients);
        model.addAttribute("users", allUsers);
        model.addAttribute("roles", Role.values());
        model.addAttribute("appointments", allAppointments);
        model.addAttribute("newUser", new User());
        return "admin-dashboard";
    }

    @PostMapping("/admin/appointment/update")
    public String updateAppointment(@ModelAttribute("editAppointment") Appointment appointment,
                                   @RequestParam Long doctorId,
                                   @RequestParam Long patientId,
                                   @RequestParam String appointmentDateTime,
                                   @RequestParam String status) {
        User doctor = userService.getUserById(doctorId);
        User patient = userService.getUserById(patientId);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);

        LocalDateTime dateTime = LocalDateTime.parse(appointmentDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        appointment.setAppointmentDate(dateTime);

        appointment.setStatus(status);

        appointmentService.updateAppointment(appointment.getId(), appointment);
        return "redirect:/admin";
    }

    @GetMapping("/admin/appointment/delete/{id}")
    public String deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return "redirect:/admin";
    }

    // ------------------ PATIENT DASHBOARD ------------------

    @GetMapping("/patient/dashboard")
    public String showPatientDashboard(@RequestParam String email,
                                       @RequestParam(required = false) Long doctorId,
                                       @RequestParam(required = false) String date,
                                       @RequestParam(required = false) String bookingError,
                                       Model model) {
        User patient = userService.getUserByEmail(email);
        List<Appointment> appointments = appointmentService.getAppointmentsByPatientEmail(email);
        List<User> doctors = userService.getAllUsers().stream()
                .filter(u -> "DOCTOR".equalsIgnoreCase(u.getRole()))
                .toList();

        model.addAttribute("patient", patient);
        model.addAttribute("appointments", appointments);
        model.addAttribute("doctors", doctors);
        model.addAttribute("bookingError", bookingError);
        model.addAttribute("selectedDoctorId", doctorId);
        model.addAttribute("selectedDate", date);

        if (doctorId != null && date != null) {
            List<String> timeSlots = java.util.stream.IntStream.rangeClosed(10, 17)
                    .mapToObj(hour -> String.format("%02d:00", hour))
                    .toList();

            // Only block slots for appointments that are not cancelled
            List<String> bookedSlots = appointmentService.getAllAppointments().stream()
                    .filter(app -> app.getDoctor().getId().equals(doctorId)
                            && app.getAppointmentDate().toLocalDate().toString().equals(date)
                            && !"Cancelled".equalsIgnoreCase(app.getStatus())
                            && !"Cancelled by Doctor".equalsIgnoreCase(app.getStatus()))
                    .map(app -> app.getAppointmentDate().toLocalTime().toString().substring(0, 5))
                    .toList();

            model.addAttribute("timeSlots", timeSlots);
            model.addAttribute("bookedSlots", bookedSlots);
        }

        return "patient-dashboard";
    }

    @PostMapping("/patient/book")
    public String bookAppointment(@RequestParam String patientEmail,
                                  @RequestParam Long doctorId,
                                  @RequestParam String appointmentDate) {
        LocalDateTime dateTime = LocalDateTime.parse(appointmentDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        if (!appointmentService.isSlotAvailable(doctorId, dateTime)) {
            return "redirect:/patient/dashboard?email=" + patientEmail + "&bookingError=Time slot is already booked.";
        }
        appointmentService.bookAppointment(doctorId, patientEmail, dateTime);
        return "redirect:/patient/dashboard?email=" + patientEmail;
    }

    @GetMapping("/patient/cancel/{appointmentId}")
    public String cancelAppointment(@PathVariable Long appointmentId,
                                    @RequestParam String email) {
        appointmentService.cancelAppointment(appointmentId);
        return "redirect:/patient/dashboard?email=" + email;
    }

    // ------------------ DOCTOR DASHBOARD ------------------

    @GetMapping("/doctor/dashboard")
    public String showDoctorDashboard(@RequestParam String email, Model model) {
        User doctor = userService.getUserByEmail(email);
        List<Appointment> appointments = appointmentService.getAppointmentsByDoctorEmail(email);

        model.addAttribute("doctor", doctor);
        model.addAttribute("appointments", appointments);
        return "doctor-dashboard";
    }

    @GetMapping("/doctor/cancel/{appointmentId}")
    public String cancelAppointmentByDoctor(@PathVariable Long appointmentId,
                                            @RequestParam String email) {
        appointmentService.cancelAppointmentByDoctor(appointmentId);
        return "redirect:/doctor/dashboard?email=" + email;
    }
}
