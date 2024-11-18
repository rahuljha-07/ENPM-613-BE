package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.CourseProgressDto;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.exception.exceptions.CantGenerateCertificateException;
import com.github.ilim.backend.exception.exceptions.StudentDidNotCompleteCourseException;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.UUID;

/**
 * Service class responsible for generating and managing certificates.
 * <p>
 * Provides functionalities to generate PDF certificates for students who have completed courses,
 * and to check a student's progress in a course.
 * </p>
 *
 * @see TemplateEngine
 * @see CourseService
 * @see StudentService
 */
@Service
@RequiredArgsConstructor
public class CertificateService {

    private final TemplateEngine templateEngine;
    private final CourseService courseService;
    private final StudentService studentService;

    /**
     * Generates a PDF certificate for a student who has completed a course.
     * <p>
     * Validates the student's completion of the course and generates a PDF certificate using Thymeleaf templates.
     * </p>
     *
     * @param student  the {@link User} entity representing the student
     * @param courseId the unique identifier of the course for which the certificate is to be generated
     * @return a byte array representing the generated PDF certificate
     * @throws StudentDidNotCompleteCourseException if the student has not completed all quizzes in the course
     * @throws CantGenerateCertificateException      if an error occurs during PDF generation
     */
    public byte[] generatePdfCertificate(User student, UUID courseId) {
        var course = courseService.findCourseByIdAndUser(student, courseId);
        var certificate = new Certificate(UUID.randomUUID(), student.getName(), course.getTitle());
        var result = studentService.getCourseQuizProgress(student, courseId);
        if (result.completedQuizzes() < result.totalQuizzes()) {
            throw new StudentDidNotCompleteCourseException(student.getId(), courseId);
        }
        try {
            return generatePdf(certificate);
        }
        catch (Exception e) {
            throw new CantGenerateCertificateException(course, e);
        }
    }

    /**
     * Generates a PDF from the provided certificate data.
     * <p>
     * Utilizes Thymeleaf to render HTML content and OpenHTMLToPDF to convert the HTML to a PDF byte array.
     * </p>
     *
     * @param certificate the {@link Certificate} record containing certificate details
     * @return a byte array representing the generated PDF
     * @throws Exception if an error occurs during PDF generation
     */
    private byte[] generatePdf(Certificate certificate) throws Exception {
        var context = new Context();
        context.setVariable("certificate", certificate);

        // Render HTML using Thymeleaf
        String htmlContent = templateEngine.process("certificate", context);

        // Convert to PDF
        var pdfStream = new ByteArrayOutputStream();
        var builder = new PdfRendererBuilder();
        builder.withHtmlContent(
            htmlContent,
            Objects.requireNonNull(getClass().getResource("/templates/")).toString()
        );
        builder.toStream(pdfStream);
        builder.run();
        return pdfStream.toByteArray();
    }

    /**
     * Checks the progress of a student in a specific course.
     * <p>
     * Retrieves the course details and returns the student's progress, including the number of completed quizzes.
     * </p>
     *
     * @param student  the {@link User} entity representing the student
     * @param courseId the unique identifier of the course
     * @return a {@link CourseProgressDto} containing the student's progress in the course
     */
    public CourseProgressDto checkCourseProgress(User student, UUID courseId) {
        // check course availability and user access
        var course = courseService.findCourseByIdAndUser(student, courseId);
        return studentService.getCourseQuizProgress(student, course.getId());
    }
}

/**
 * Record representing a certificate.
 *
 * @param certificateId the unique identifier of the certificate
 * @param studentName   the name of the student receiving the certificate
 * @param courseTitle    the title of the course for which the certificate is issued
 */
record Certificate(UUID certificateId, String studentName, String courseTitle) {}
