package ru.netology.patient.service.medical;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.alert.SendAlertServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


public class TestMedicalServiceImpl {

    @Test
    void checkBloodPressureWarning() {
    PatientInfo patientInfo = new PatientInfo(UUID.randomUUID().toString(),"Иван", "Петров",
            LocalDate.of(1980, 11, 26), new HealthInfo(new BigDecimal("36.65"),
            new BloodPressure(120, 80)));
    PatientInfoFileRepository patientInfoFileRepository = Mockito.mock(PatientInfoFileRepository.class);
    String message = String.format("Warning, patient with id: %s, need help", patientInfo.getId());
    Mockito.when(patientInfoFileRepository.getById(patientInfo.getId()))
            .thenReturn(patientInfo);
    SendAlertService sendAlertService = Mockito.mock(SendAlertServiceImpl.class);
    MedicalService medicalService = new MedicalServiceImpl(patientInfoFileRepository, sendAlertService);
    BloodPressure currentPressure = new BloodPressure(60, 120);
    medicalService.checkBloodPressure(patientInfo.getId(), currentPressure);
    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
    Mockito.verify(sendAlertService).send(argumentCaptor.capture());
    Assertions.assertEquals(message, argumentCaptor.getValue());
    }
}
