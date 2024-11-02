package com.github.ilim.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "user_answers")
@NoArgsConstructor
public class UserAnswer extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "attempt_id")
    private QuizAttempt attempt;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question questions;


    @OneToMany(mappedBy = "userAnswer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAnswerOption> selectedOptions;

    @Column(nullable = false)
    private boolean isCorrect;

    @Column(nullable = false)
    private BigDecimal answer;
}
