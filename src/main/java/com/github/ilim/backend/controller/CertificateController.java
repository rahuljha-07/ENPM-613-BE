package com.github.ilim.backend.controller;

import com.github.ilim.backend.service.CertificateService;
import com.github.ilim.backend.service.UserService;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Reply;
import com.github.ilim.backend.util.response.Res;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;
    private final UserService userService;

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
