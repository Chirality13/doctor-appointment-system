package com.chirag.doctorappointmentsystem.service;

import com.chirag.doctorappointmentsystem.model.Appointment;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {
    Appointment createAppointment(Appointment appointment);
    List<Appointment> getAllAppointments();
    Appointment getAppointmentById(Long id);
    void deleteAppointment(Long id);
    Appointment updateAppointment(Long id, Appointment appointment);

    List<Appointment> getAppointmentsByPatientEmail(String email);
    List<Appointment> getAppointmentsByDoctorEmail(String email);

    Appointment bookAppointment(Long doctorId, String patientEmail, LocalDateTime dateTime);

    boolean isSlotAvailable(Long doctorId, LocalDateTime dateTime);

    void cancelAppointment(Long appointmentId);
    void cancelAppointmentByDoctor(Long appointmentId);
    
}
