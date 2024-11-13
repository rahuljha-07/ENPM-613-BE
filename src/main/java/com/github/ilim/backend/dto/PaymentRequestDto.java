package com.github.ilim.backend.dto;

import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.User;
import lombok.Data;

@Data
public class PaymentRequestDto {
    private String userId;
    private String courseId;
    private String courseName;
    private String courseDescription;
    private Double coursePrice;
    private String currency;

    public static PaymentRequestDto createRequestDto(User student, Course course, String defaultCurrency) {
        var dto = new PaymentRequestDto();
        dto.setCourseId(course.getId().toString());
        dto.setCourseName(course.getTitle());
        dto.setCourseDescription(course.getDescription());
        dto.setCoursePrice(course.getPrice().doubleValue());
        dto.setCurrency(defaultCurrency);
        dto.setUserId(student.getId());
        return dto;
    }
}
