package com.github.ilim.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
@Entity
@Table(name = "user_answer_options")
@NonNull
public class UserAnswerOption extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    private UserAnswer userAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id", nullable = false)
    private QuestionOption selectedOption;

    public static UserAnswerOption from(UserAnswer answer, QuestionOption option) {
        var answerOption = new UserAnswerOption();
        answerOption.setUserAnswer(answer);
        answerOption.setSelectedOption(option);
        return answerOption;
    }
}
