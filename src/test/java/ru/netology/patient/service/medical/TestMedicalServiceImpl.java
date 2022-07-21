package ru.netology.patient.service.medical;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.alert.SendAlertServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Stream;


public class TestMedicalServiceImpl {

    @Test
    void checkBloodPressureMessage() {
    PatientInfo patientInfo = new PatientInfo(UUID.randomUUID().toString(),"Иван", "Петров",
            LocalDate.of(1980, 11, 26), new HealthInfo(new BigDecimal("36.65"),
            new BloodPressure(120, 80)));
    String message = String.format("Warning, patient with id: %s, need help", patientInfo.getId());
    PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
    Mockito.when(patientInfoRepository.getById(patientInfo.getId()))
            .thenReturn(patientInfo);
    SendAlertService sendAlertService = Mockito.spy(SendAlertServiceImpl.class);
    MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);
    BloodPressure currentPressure = new BloodPressure(60, 120);
    medicalService.checkBloodPressure(patientInfo.getId(), currentPressure);
    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
    Mockito.verify(sendAlertService).send(argumentCaptor.capture());
    Assertions.assertEquals(message, argumentCaptor.getValue());
    }

    @ParameterizedTest
    @MethodSource("source")
    void checkBloodPressureAlertWorking(BloodPressure currentPressure, int wantedNumber) {
        PatientInfo patientInfo = new PatientInfo(UUID.randomUUID().toString(),"Иван", "Петров",
                LocalDate.of(1980, 11, 26), new HealthInfo(new BigDecimal("36.65"),
                new BloodPressure(120, 80)));
        String message = String.format("Warning, patient with id: %s, need help", patientInfo.getId());
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById(patientInfo.getId()))
                .thenReturn(patientInfo);
        SendAlertService sendAlertService = Mockito.spy(SendAlertServiceImpl.class);
        MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);
        medicalService.checkBloodPressure(patientInfo.getId(), currentPressure);
        Mockito.verify(sendAlertService, Mockito.times(wantedNumber)).send(message);
    }

    private static Stream<Arguments> source() {
        return Stream.of(
                Arguments.of(new BloodPressure(60, 120), 1),
                Arguments.of(new BloodPressure(120, 80), 0)
        );
    }

    @ParameterizedTest
    @MethodSource("source1")
    void checkCheckTemperatureAlertWorking(BigDecimal currentTemperature, int wantedNumber) {
        PatientInfo patientInfo = new PatientInfo(UUID.randomUUID().toString(),"Иван", "Петров",
                LocalDate.of(1980, 11, 26), new HealthInfo(new BigDecimal("36.65"),
                new BloodPressure(120, 80)));
        String message = String.format("Warning, patient with id: %s, need help", patientInfo.getId());
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById(patientInfo.getId()))
                .thenReturn(patientInfo);
        SendAlertService sendAlertService = Mockito.spy(SendAlertServiceImpl.class);
        MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);
        medicalService.checkTemperature(patientInfo.getId(), currentTemperature);
        Mockito.verify(sendAlertService, Mockito.times(wantedNumber)).send(message);
    }

    private static Stream<Arguments> source1() {
        return Stream.of(
                Arguments.of(new BigDecimal("34.9"), 1),
                Arguments.of(new BigDecimal("36.6"), 0)
        );
    }
}
