package com.github.ilim.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table(name = "quiz_attempts")
@NoArgsConstructor
@JsonIgnoreProperties({"student", "quiz"})
public class QuizAttempt extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    private BigDecimal userScore;
    private BigDecimal totalScore;
    private boolean isPassed;

    @JsonProperty
    public String getStudentId() {
        return student.getId();
    }

    @JsonProperty
    public UUID geQuizId() {
        return quiz.getId();
    }

    public static QuizAttempt from(User user, Quiz quiz) {
        var attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setStudent(user);
        return attempt;
    }
}
