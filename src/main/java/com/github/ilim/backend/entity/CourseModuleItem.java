package com.github.ilim.backend.entity;

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
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Table(name = "course_module_items")
@NoArgsConstructor
public class CourseModuleItem extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_module_id")
    private CourseModule courseModule;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ModuleItemType itemType;

    @Column(nullable = false)
    private UUID itemId;

    @Column(nullable = false)
    private int orderIndex;

}
