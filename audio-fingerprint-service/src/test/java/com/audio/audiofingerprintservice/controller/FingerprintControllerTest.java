package com.audio.audiofingerprintservice.controller;

import com.audio.audiofingerprintservice.dto.FingerprintMatchResponse;
import com.audio.audiofingerprintservice.service.FingerprintService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FingerprintController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
                org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
        })
public class FingerprintControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FingerprintService fingerprintService;

    @Test
    void searchFingerprint_ShouldReturnMatches() throws Exception {
        // Подготовка тестовых данных
        MockMultipartFile file = new MockMultipartFile(
                "audio",
                "test.mp3",
                "audio/mpeg",
                "test audio content".getBytes()
        );

        // Мокирование сервиса
        FingerprintMatchResponse mockResponse = new FingerprintMatchResponse();
        given(fingerprintService.searchMatch(any())).willReturn(mockResponse);

        // Выполнение запроса и проверка результата
        mockMvc.perform(multipart("/api/v1/fingerprints/search")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }
}