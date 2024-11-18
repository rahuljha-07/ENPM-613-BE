package com.github.ilim.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ilim.backend.dto.CourseDto;
import com.github.ilim.backend.enums.CourseStatus;
import com.github.ilim.backend.exception.exceptions.CourseModuleNotFoundException;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a course in the system.
 * <p>
 * Contains course details such as title, thumbnail URL, description, price, status, deletion flag,
 * associated modules, and the instructor.
 * </p>
 */
@Getter
@Setter
@Entity
@Table(name = "courses")
@NoArgsConstructor
public class Course extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    private String thumbnailUrl;

    @Column(length = 2000)
    private String description;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "orderIndex")
    private List<CourseModule> courseModules = new ArrayList<>();

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private CourseStatus status = CourseStatus.DRAFT;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    @JsonIgnore
    private User instructor;

    @JsonProperty("instructorId")
    public String getInstructorId() {
        return instructor.getId(); // shouldn't check for null
    }

    public void setStatus(CourseStatus status) {
        this.status = status != null ? status : CourseStatus.DRAFT;
    }

    /**
     * Creates a {@link Course} entity from a {@link CourseDto}.
     *
     * @param dto the {@link CourseDto} containing course details
     * @return a new {@link Course} entity populated with data from the DTO
     */
    public static Course from(CourseDto dto) {
        var course = new Course();
        course.setTitle(dto.getTitle());
        course.setThumbnailUrl(dto.getThumbnailUrl());
        course.setDescription(dto.getDescription());
        course.setPrice(dto.getPrice());
        return course;
    }

    /**
     * Updates the course's details based on the provided {@link CourseDto}.
     *
     * @param dto the {@link CourseDto} containing updated course details
     */
    public void updateFrom(@Valid CourseDto dto) {
        setTitle(dto.getTitle());
        setThumbnailUrl(dto.getThumbnailUrl());
        setDescription(dto.getDescription());
        setPrice(dto.getPrice());
    }

    /**
     * Adds a new module to the course.
     * <p>
     * Sets the order index of the module, associates it with the course,
     * and adds it to the course's module list.
     * </p>
     *
     * @param module the {@link CourseModule} entity to add
     */
    public void addCourseModule(CourseModule module) {
        module.setOrderIndex(courseModules.size());
        courseModules.add(module);
        module.setCourse(this);
    }

    /**
     * Deletes a module from the course.
     * <p>
     * Removes the specified module, disassociates it from the course,
     * and re-indexes the remaining modules.
     * </p>
     *
     * @param module the {@link CourseModule} entity to delete
     * @throws CourseModuleNotFoundException if the module is not found within the course
     */
    public void deleteCourseModule(CourseModule module) {
        int deletedIndex = module.getOrderIndex();
        courseModules.remove(module);
        module.setCourse(null);

        // Re-index the modules from the removed index
        for (int i = deletedIndex; i < courseModules.size(); ++i) {
            courseModules.get(i).setOrderIndex(i);
        }
    }

    /**
     * Finds a module within the course by its unique identifier.
     *
     * @param moduleId the unique identifier of the module to find
     * @return the {@link CourseModule} entity corresponding to the provided ID
     * @throws CourseModuleNotFoundException if no module is found with the given ID
     */
    public CourseModule findModule(UUID moduleId) {
        return courseModules.stream().filter(mod -> mod.getId().equals(moduleId)).findFirst().orElseThrow(() -> new CourseModuleNotFoundException(moduleId));
    }

}

