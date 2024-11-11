package com.github.ilim.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ilim.backend.enums.PurchaseStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "course_purchases")
@NoArgsConstructor
public class CoursePurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    @JsonBackReference
    private Course course;

    @Column(nullable = false)
    private LocalDateTime purchaseDate;

    @Column(nullable = false)
    private BigDecimal purchasePrice;

    private String paymentId;

    private String failedMessage;

    @Column(nullable = false)
    private PurchaseStatus status = PurchaseStatus.PENDING;

    @JsonProperty("studentId")
    public String getStudentId() {
        return student.getId();
    }

    @JsonProperty("courseId")
    public UUID geCourseId() {
        return course.getId();
    }
}
