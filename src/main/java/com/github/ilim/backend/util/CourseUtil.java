package com.github.ilim.backend.util;

import com.github.ilim.backend.dto.PublicCourseDto;
import com.github.ilim.backend.dto.StudentCourseDto;
import com.github.ilim.backend.entity.Course;

import java.util.List;

/**
 * Utility class for converting {@link Course} entities to various Data Transfer Objects (DTOs).
 * <p>
 * Provides methods to convert courses into public-facing DTOs and student-specific DTOs.
 * </p>
 */
public class CourseUtil {

    /**
     * Converts a {@link Course} entity to a {@link PublicCourseDto}.
     *
     * @param course the {@link Course} entity to convert
     * @return a {@link PublicCourseDto} representing the public view of the course
     */
    public static PublicCourseDto toPublicCourseDto(Course course) {
        return PublicCourseDto.from(course);
    }

    /**
     * Converts a list of {@link Course} entities to a list of {@link PublicCourseDto}s.
     *
     * @param courses the list of {@link Course} entities to convert
     * @return a list of {@link PublicCourseDto}s representing the public view of the courses
     */
    public static List<PublicCourseDto> toPublicCourseDtos(List<Course> courses) {
        return courses.stream().map(CourseUtil::toPublicCourseDto).toList();
    }

    /**
     * Converts a {@link Course} entity to a {@link StudentCourseDto}.
     *
     * @param course the {@link Course} entity to convert
     * @return a {@link StudentCourseDto} representing the course view for students
     */
    public static StudentCourseDto toStudentCourseDto(Course course) {
        return StudentCourseDto.from(course);
    }

    /**
     * Converts a list of {@link Course} entities to a list of {@link StudentCourseDto}s.
     *
     * @param courses the list of {@link Course} entities to convert
     * @return a list of {@link StudentCourseDto}s representing the course view for students
     */
    public static List<StudentCourseDto> toStudentCourseDtos(List<Course> courses) {
        return courses.stream().map(CourseUtil::toStudentCourseDto).toList();
    }
}
