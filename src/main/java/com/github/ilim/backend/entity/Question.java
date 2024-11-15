package com.github.ilim.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ilim.backend.dto.QuestionDto;
import com.github.ilim.backend.enums.QuestionType;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "questions")
@NoArgsConstructor
@JsonIgnoreProperties({"quiz"})
public class Question extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    @JsonIgnore
    private Quiz quiz;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionOption> options;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private QuestionType type;

    @Column(nullable = false)
    private BigDecimal points;

    @Column(nullable = false)
    private int orderIndex;

    public static Question from(QuestionDto dto) {
        Question question = new Question();
        question.setText(dto.getText());
        question.setType(dto.getType());
        question.setPoints(dto.getPoints());
        return question;
    }

    @JsonProperty("quizId")
    public UUID getQuizId() {
        return quiz.getId();
    }

}
