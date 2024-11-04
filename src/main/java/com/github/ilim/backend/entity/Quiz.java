package com.github.ilim.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.ilim.backend.dto.QuizDto;
import jakarta.persistence.Column;
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

import java.util.UUID;

@Data
@Entity
@Table(name = "quizzes")
@NoArgsConstructor
@JsonIgnoreProperties({"courseModule"})
public class Quiz extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(length = 5000)
    private String description;

    @Column(nullable = false)
    private int passingScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private CourseModule courseModule;

    public static Quiz from(QuizDto dto) {
        Quiz quiz = new Quiz();
        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setPassingScore(dto.getPassingScore());
        return quiz;
    }

    public void updateFrom(QuizDto dto) {
        title = dto.getTitle();
        description = dto.getDescription();
        passingScore = dto.getPassingScore();
    }
}