package com.github.ilim.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.ilim.backend.dto.QuestionOptionDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "question_options")
@NoArgsConstructor
@JsonIgnoreProperties({"question"})
public class QuestionOption extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(nullable = false)
    private String text;

    private Boolean isCorrect;

    @Column(nullable = false)
    private int orderIndex;

    public static QuestionOption from(QuestionOptionDto dto) {
        QuestionOption option = new QuestionOption();
        option.setText(dto.getText());
        option.setIsCorrect(dto.getIsCorrect());
        return option;
    }

}
