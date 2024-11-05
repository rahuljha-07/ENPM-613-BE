package com.github.ilim.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id")
    private QuizAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @OneToMany(mappedBy = "userAnswer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAnswerOption> selectedOptions;

    @Column(nullable = false)
    private boolean isCorrect;

    public static UserAnswer from(Question question, QuizAttempt attempt) {
        UserAnswer answer = new UserAnswer();
        answer.setAttempt(attempt);
        answer.setQuestion(question);
        return answer;
    }
}
