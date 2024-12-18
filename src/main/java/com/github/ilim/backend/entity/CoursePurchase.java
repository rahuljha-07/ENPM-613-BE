package com.github.ilim.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ilim.backend.enums.PurchaseStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "course_purchases")
@NoArgsConstructor
public class CoursePurchase extends AuditEntity {

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
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime purchaseDate;

    @Column(nullable = false)
    private BigDecimal purchasePrice;

    private String paymentId;

    private String failedMessage;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PurchaseStatus status = PurchaseStatus.PENDING;

    public static CoursePurchase createPendingPurchase(User student, Course course) {
        var purchase = new CoursePurchase();
        purchase.setStudent(student);
        purchase.setCourse(course);
        purchase.setPurchasePrice(course.getPrice());
        purchase.setPurchaseDate(LocalDateTime.now());
        purchase.setStatus(PurchaseStatus.PENDING);
        return purchase;
    }

    @JsonProperty("studentId")
    public String getStudentId() {
        return student.getId();
    }

    @JsonProperty("courseId")
    public UUID geCourseId() {
        return course.getId();
    }
}
