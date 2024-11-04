package com.github.ilim.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.ilim.backend.enums.ModuleItemType;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Table(name = "course_module_items")
@NoArgsConstructor
@JsonIgnoreProperties({"courseModule"})
public class CourseModuleItem extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_module_id")
    @JsonIgnore
    private CourseModule courseModule;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ModuleItemType itemType;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "video_id")
    private Video video;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @Column(nullable = false)
    private int orderIndex;

    public static CourseModuleItem create(Video video) {
        var item = new CourseModuleItem();
        item.setCourseModule(video.getCourseModule());
        item.itemType = ModuleItemType.VIDEO;
        item.video = video;
        return item;
    }

    public static CourseModuleItem create(Quiz quiz) {
        var item = new CourseModuleItem();
        item.setCourseModule(quiz.getCourseModule());
        item.itemType = ModuleItemType.QUIZ;
        item.quiz = quiz;
        return item;
    }
}
