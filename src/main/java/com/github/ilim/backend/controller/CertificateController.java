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


@RestController
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;
    private final UserService userService;


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

    private static HttpHeaders createPdfResponseHeaders(@NonNull String filename) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return headers;
    }
}
