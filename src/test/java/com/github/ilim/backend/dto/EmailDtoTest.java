package com.github.ilim.backend.dto;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EmailDtoTest {

    @Test
    void testEmailDtoFields() {
        EmailDto dto = new EmailDto();
        dto.setToAddress("recipient@example.com");
        List<String> cc = Arrays.asList("cc1@example.com", "cc2@example.com");
        dto.setCcAddresses(cc);
        dto.setSubject("Test Email");
        dto.setContent("This is a test email.");

        assertEquals("recipient@example.com", dto.getToAddress());
        assertEquals(cc, dto.getCcAddresses());
        assertEquals("Test Email", dto.getSubject());
        assertEquals("This is a test email.", dto.getContent());
    }
}
