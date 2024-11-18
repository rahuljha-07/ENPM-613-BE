package com.github.ilim.backend.controller;

import com.github.ilim.backend.dto.CourseProgressDto;
import com.github.ilim.backend.service.CertificateService;
import com.github.ilim.backend.service.UserService;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Reply;
import com.github.ilim.backend.util.response.Res;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for handling certificate-related operations.
 * <p>
 * Provides endpoints for checking course progress and generating PDF certificates for students.
 * </p>
 *
 * @see CertificateService
 * @see UserService
 */
@RestController
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;
    private final UserService userService;


    /**
     * Checks the progress of a user in a specific course.
     * <p>
     * Extracts the user's information from the JWT, invokes the {@link CertificateService#checkCourseProgress(User, UUID)}
     * method, and returns the course progress details.
     * </p>
     *
     * @param jwt      the JWT token containing the user's authentication details
     * @param courseId the ID of the course to check progress for
     * @return an {@link ApiRes} containing the {@link CourseProgressDto} with progress details
     */
    @GetMapping("/student/course/{courseId}/check-progress")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
    public ApiRes<Res<CourseProgressDto>> checkCourseProgress(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable @NonNull UUID courseId
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        var progress = certificateService.checkCourseProgress(user, courseId);
        return Reply.ok(progress);
    }

    /**
     * Generates a PDF certificate for a user upon course completion.
     * <p>
     * Extracts the user's information from the JWT, invokes the {@link CertificateService#generatePdfCertificate(User, UUID)}
     * method to generate the certificate, and returns the PDF as a downloadable file.
     * </p>
     *
     * @param jwt      the JWT token containing the user's authentication details
     * @param courseId the ID of the course for which the certificate is to be generated
     * @return a {@link ResponseEntity} containing the PDF certificate as a byte array
     */
    @PostMapping("/student/course/{courseId}/certificate")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
    public ResponseEntity<byte[]> generateCertificate(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable("courseId") UUID courseId
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        var pdfCertificateBytes = certificateService.generatePdfCertificate(user, courseId);
        return ResponseEntity.ok()
            .headers(createPdfResponseHeaders("certificate.pdf"))
            .body(pdfCertificateBytes);
    }

    /**
     * Creates HTTP headers for the PDF response.
     * <p>
     * Sets the content type to PDF, specifies the content disposition as an attachment with the given filename,
     * and configures cache control.
     * </p>
     *
     * @param filename the name of the PDF file to be downloaded
     * @return the configured {@link HttpHeaders} instance
     */
    private static HttpHeaders createPdfResponseHeaders(@NonNull String filename) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return headers;
    }
}
