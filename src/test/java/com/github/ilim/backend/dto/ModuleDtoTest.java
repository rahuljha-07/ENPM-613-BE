package com.github.ilim.backend.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModuleDtoTest {

    @Test
    void testModuleDtoFields() {
        ModuleDto dto = new ModuleDto();
        dto.setTitle("Introduction to Java");
        dto.setDescription("Basics of Java programming.");
        dto.setOrderIndex(1);

        assertEquals("Introduction to Java", dto.getTitle());
        assertEquals("Basics of Java programming.", dto.getDescription());
        assertEquals(1, dto.getOrderIndex());
    }
}
