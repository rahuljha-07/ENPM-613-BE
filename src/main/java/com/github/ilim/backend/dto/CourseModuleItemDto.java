package com.github.ilim.backend.dto;

import com.github.ilim.backend.entity.CourseModuleItem;
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

    public static CourseModuleItemDto from(CourseModuleItem item) {
        var dto = new CourseModuleItemDto();
        dto.setId(item.getId());
        dto.setCourseModuleId(item.getCourseModule().getId());
        dto.setItemId(item.getId());
        dto.setItemType(item.getItemType());
        dto.setOrderIndex(item.getOrderIndex());
        if (item.getItemType() == ModuleItemType.QUIZ) {
            dto.setPayload(StudentQuizDto.from(item.getQuiz()));
        }
        else if (item.getItemType() == ModuleItemType.VIDEO) {
            dto.setPayload(VideoDto.from(item.getVideo()));
        }
        else {
            throw new IllegalArgumentException("Unsupported CourseModuleItem type: " + item.getItemType());
        }
        return dto;
    }
}
