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

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final TemplateEngine templateEngine;
    private final CourseService courseService;
    private final StudentService studentService;

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

    public CourseProgressDto checkCourseProgress(User student, UUID courseId) {
        // check course availability and user access
        var course = courseService.findCourseByIdAndUser(student, courseId);
        return studentService.getCourseQuizProgress(student, course.getId());
    }
}

record Certificate(UUID certificateId, String studentName, String courseTitle) {}