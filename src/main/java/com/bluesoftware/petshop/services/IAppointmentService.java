package com.bluesoftware.petshop.services;

import com.bluesoftware.petshop.entities.Appointment;

import java.time.LocalDateTime;
import java.util.*;

public interface IAppointmentService {

    Appointment createAppointment(Integer customerId,
                                  Integer petId,
                                  Integer veterinarianId,
                                  LocalDateTime dateTime,
                                  String reason);

    List<Appointment> getAllAppointments();

    Appointment getAppointmentById(Integer id);

    Appointment updateAppointmentStatus(Integer id, String status);

    void deleteAppointment(Integer id);
}