package com.bluesoftware.petshop.services;

import com.bluesoftware.petshop.entities.*;
import com.bluesoftware.petshop.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentServiceImpl implements IAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final PetRepository petRepository;
    private final VeterinarianRepository veterinarianRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  CustomerRepository customerRepository,
                                  PetRepository petRepository,
                                  VeterinarianRepository veterinarianRepository) {
        this.appointmentRepository = appointmentRepository;
        this.customerRepository = customerRepository;
        this.petRepository = petRepository;
        this.veterinarianRepository = veterinarianRepository;
    }

    @Transactional
    @Override
    public Appointment createAppointment(Integer customerId,
                                          Integer petId,
                                          Integer veterinarianId,
                                          LocalDateTime dateTime,
                                          String reason) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        Veterinarian veterinarian = null;
        if (veterinarianId != null) {
            veterinarian = veterinarianRepository.findById(veterinarianId)
                    .orElseThrow(() -> new RuntimeException("Veterinarian not found"));
        }

        Appointment appointment = Appointment.builder()
                .customer(customer)
                .pet(pet)
                .veterinarian(veterinarian)
                .dateTime(dateTime)
                .reason(reason)
                .status(AppointmentStatus.PENDING)
                .active(true)
                .build();

        return appointmentRepository.save(appointment);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    @Override
    public Appointment getAppointmentById(Integer id) {
        return appointmentRepository.findById(id)
                .filter(Appointment::isActive)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    @Transactional
    @Override
    public Appointment updateAppointmentStatus(Integer id, String status) {

        Appointment appointment = getAppointmentById(id);

        try {
            appointment.setStatus(AppointmentStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid appointment status");
        }

        return appointmentRepository.save(appointment);
    }

    @Transactional
    @Override
    public void deleteAppointment(Integer id) {
        Appointment appointment = getAppointmentById(id);
        appointment.setActive(false);
        appointmentRepository.save(appointment);
    }
}