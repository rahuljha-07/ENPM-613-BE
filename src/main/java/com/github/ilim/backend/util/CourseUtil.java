package com.github.ilim.backend.util;

import com.github.ilim.backend.dto.PublicCourseDto;
import com.github.ilim.backend.dto.StudentCourseDto;
import com.github.ilim.backend.entity.Course;

import java.util.List;

public class CourseUtil {

    public static PublicCourseDto toPublicCourseDto(Course course) {
        return PublicCourseDto.from(course);
    }

    public static List<PublicCourseDto> toPublicCourseDtos(List<Course> courses) {
        return courses.stream().map(CourseUtil::toPublicCourseDto).toList();
    }

    public static StudentCourseDto toStudentCourseDto(Course course) {
        return StudentCourseDto.from(course);
    }

    public static List<StudentCourseDto> toStudentCourseDtos(List<Course> courses) {
        return courses.stream().map(CourseUtil::toStudentCourseDto).toList();
    }
}
