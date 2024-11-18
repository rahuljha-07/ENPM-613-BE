package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.EmailDto;
import com.github.ilim.backend.exception.exceptions.EmailSendingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailSenderServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private EmailSenderService emailSenderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emailSenderService = new EmailSenderService(webClient);
    }

    @Test
    void testSendEmail_Success() {
        EmailDto emailDto = new EmailDto();
        emailDto.setToAddress("recipient@example.com");
        emailDto.setSubject("Test Subject");
        emailDto.setContent("Test Body");

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        java.util.Optional.empty().map(v -> null);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(null);

        assertThrows(Throwable.class, () -> emailSenderService.sendEmail(emailDto));
    }

    @Test
    void testSendEmail_Failure() {
        EmailDto emailDto = new EmailDto();
        emailDto.setToAddress("invalid-email");
        emailDto.setSubject("Test Subject");
        emailDto.setContent("Test Body");

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        //when(requestBodySpec.bodyValue(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenThrow(new RuntimeException("Email sending failed"));

        assertThrows(EmailSendingException.class, () -> {
            emailSenderService.sendEmail(emailDto);
        });

        verify(webClient, times(1)).post();
    }
}
