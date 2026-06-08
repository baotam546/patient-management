package com.pm.patient_service.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pm.patient_service.dto.PatientRequestDTO;
import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.exception.EmailAlreadyExistsException;
import com.pm.patient_service.exception.PatientNotFoundException;
import com.pm.patient_service.mapper.PatientMapper;
import com.pm.patient_service.model.Patient;
import com.pm.patient_service.repository.PatientRepository;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDTO> getPatients(){
        List<Patient> patients = patientRepository.findAll();

        List<PatientResponseDTO> patientResponseDTOs = patients.stream()
                .map(patient -> PatientMapper.toDTO(patient))
                .toList();

        return patientResponseDTOs;
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO){
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            throw new EmailAlreadyExistsException("Email " + patientRequestDTO.getEmail() + " already exists");
        }
        Patient patient = PatientMapper.toEntity(patientRequestDTO);
        Patient savedPatient = patientRepository.save(patient);
        return PatientMapper.toDTO(savedPatient);
    }

    public PatientResponseDTO getPatientById(UUID id){
        // UUID uuid = UUID.fromString(id);
        // if(!patientRepository.existsById(id)){
        //     throw new PatientNotFoundException("Patient with id " + id + " not found");
        // }
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException("Patient with id " + id + " not found"));
        return PatientMapper.toDTO(patient);
    }

    public PatientResponseDTO updatePatient(String id, PatientRequestDTO patientRequestDTO){
        // logger.warn("Patient with id " + id + " not found");
        // if(!patientRepository.existsById(id)){
        //     throw new PatientNotFoundException("Patient with id " + id + " not found");
        // }
        UUID uuid = UUID.fromString(id);
        Patient patient = patientRepository.findById(uuid).orElseThrow(() -> new PatientNotFoundException("Patient with id " + id + " not found"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateOfBirth = LocalDate.parse(patientRequestDTO.getDateOfBirth(), formatter);
        // LocalDate registeredDate = LocalDate.parse(patientRequestDTO.getRegisteredDate(), formatter);
        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(dateOfBirth);
        // patient.setRegisteredDate(registeredDate);
        Patient updatedPatient = patientRepository.save(patient);
        return PatientMapper.toDTO(updatedPatient);
    }
}
