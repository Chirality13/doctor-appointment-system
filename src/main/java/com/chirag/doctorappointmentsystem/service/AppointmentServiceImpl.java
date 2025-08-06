package com.chirag.doctorappointmentsystem.service;

import com.chirag.doctorappointmentsystem.model.Appointment;
import com.chirag.doctorappointmentsystem.model.User;
import com.chirag.doctorappointmentsystem.repository.AppointmentRepository;
import com.chirag.doctorappointmentsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Appointment createAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    @Override
    public Appointment updateAppointment(Long id, Appointment appointment) {
        Appointment existing = appointmentRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setDoctor(appointment.getDoctor());
            existing.setPatient(appointment.getPatient());
            existing.setAppointmentDate(appointment.getAppointmentDate());
            existing.setStatus(appointment.getStatus());
            return appointmentRepository.save(existing);
        }
        return null;
    }

    @Override
    public List<Appointment> getAppointmentsByPatientEmail(String email) {
        return appointmentRepository.findAll().stream()
                .filter(app -> app.getPatient() != null && email.equals(app.getPatient().getEmail()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Appointment> getAppointmentsByDoctorEmail(String email) {
        return appointmentRepository.findAll().stream()
                .filter(app -> app.getDoctor() != null && email.equals(app.getDoctor().getEmail()))
                .collect(Collectors.toList());
    }

    @Override
    public Appointment bookAppointment(Long doctorId, String patientEmail, LocalDateTime dateTime) {
        User doctor = userRepository.findById(doctorId).orElse(null);
        User patient = userRepository.findByEmail(patientEmail).orElse(null);

        if (doctor == null || patient == null || !isSlotAvailable(doctorId, dateTime)) {
            return null;
        }

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentDate(dateTime);
        appointment.setStatus("Booked");

        return appointmentRepository.save(appointment);
    }

    @Override
    public boolean isSlotAvailable(Long doctorId, LocalDateTime dateTime) {
        User doctor = userRepository.findById(doctorId).orElse(null);
        if (doctor == null) return false;
        List<Appointment> appointments = appointmentRepository.findByDoctorAndAppointmentDate(doctor, dateTime);
        for (Appointment app : appointments) {
            if (!"Cancelled".equalsIgnoreCase(app.getStatus()) && !"Cancelled by Doctor".equalsIgnoreCase(app.getStatus())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointment != null) {
            appointment.setStatus("Cancelled");
            appointmentRepository.save(appointment);
        }
    }

    @Override
    public void cancelAppointmentByDoctor(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointment != null) {
            appointment.setStatus("Cancelled by Doctor");
            appointmentRepository.save(appointment);
        }
    }
}
