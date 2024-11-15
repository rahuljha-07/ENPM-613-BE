package com.github.ilim.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ilim.backend.dto.ModuleDto;
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
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "course_modules")
@NoArgsConstructor
@JsonIgnoreProperties({"course"})
public class CourseModule extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private int orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    @JsonIgnore
    private Course course;

    @OneToMany(mappedBy = "courseModule", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "orderIndex")
    private List<CourseModuleItem> moduleItems = new ArrayList<>();

    @JsonProperty("courseId")
    public UUID getCourseId() {
        return course.getId();
    }

    public static CourseModule from(ModuleDto dto) {
        var module = new CourseModule();
        module.setTitle(dto.getTitle());
        module.setDescription(dto.getDescription());
        return module;
    }

    public void addModuleItem(CourseModuleItem item) {
        item.setOrderIndex(moduleItems.size() + 1);
        moduleItems.add(item);
        item.setCourseModule(this);
    }

    public void removeModuleItem(CourseModuleItem item) {
        moduleItems.remove(item);
        item.setCourseModule(null);
    }

    public void updateFrom(ModuleDto dto) {
        title = dto.getTitle();
        description = dto.getDescription();

        // use dtoIndex only if it's different and within the range, otherwise ignore it
        var dtoIndex = dto.getOrderIndex();
        int modulesSize = course.getCourseModules().size();
        if (dtoIndex != null && dtoIndex != orderIndex) {
            if (dtoIndex >= 0 && dtoIndex < modulesSize) { // Changed to < modulesSize
                if (dtoIndex < orderIndex) {
                    // moving up: Shift modules between dtoIndex and orderIndex -1 down by 1
                    for (int i = dtoIndex; i < orderIndex; ++i) {
                        course.getCourseModules().get(i).setOrderIndex(i + 1);
                    }
                } else {
                    // moving down: Shift modules between orderIndex +1 and dtoIndex up by 1
                    for (int i = orderIndex + 1; i <= dtoIndex; ++i) {
                        course.getCourseModules().get(i).setOrderIndex(i - 1);
                    }
                }
                // set the current module's orderIndex to dtoIndex
                this.orderIndex = dtoIndex;
                // no need to manually sort modules due to @OrderColumn
            }
        }
    }

}
