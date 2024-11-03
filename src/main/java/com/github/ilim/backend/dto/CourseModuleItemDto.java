package com.github.ilim.backend.dto;

import com.github.ilim.backend.enums.ModuleItemType;
import lombok.Data;

import java.util.UUID;

@Data
public class CourseModuleItemDto {
    private UUID id;
    private UUID courseModuleId;
    private UUID itemId;
    private ModuleItemType itemType;
    private int orderIndex;
    private Object payload;
}
