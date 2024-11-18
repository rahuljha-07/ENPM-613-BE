package com.github.ilim.backend.entity;

import com.github.ilim.backend.dto.ModuleDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourseModuleTest {

    @Test
    void testFromDto() {
        ModuleDto dto = new ModuleDto();
        dto.setTitle("Module 1");
        dto.setDescription("Module Description");

        CourseModule module = CourseModule.from(dto);

        assertEquals(dto.getTitle(), module.getTitle());
        assertEquals(dto.getDescription(), module.getDescription());
    }

    @Test
    void testAddAndRemoveModuleItem() {
        CourseModule module = new CourseModule();
        CourseModuleItem item = new CourseModuleItem();

        module.addModuleItem(item);
        assertEquals(1, module.getModuleItems().size());

        module.removeModuleItem(item);
        assertEquals(0, module.getModuleItems().size());
    }
}
