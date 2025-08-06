package com.chirag.doctorappointmentsystem.repository;

import com.chirag.doctorappointmentsystem.model.Appointment;
import com.chirag.doctorappointmentsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatient(User patient);

    List<Appointment> findByDoctorAndAppointmentDate(User doctor, LocalDateTime appointmentDate);

    boolean existsByDoctorAndAppointmentDate(User doctor, LocalDateTime appointmentDate);

    // New method to find all appointments by doctor user object
    List<Appointment> findByDoctor(User doctor);
}
