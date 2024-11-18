package com.github.ilim.backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.ilim.backend.util.AuditListener;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

/**
 * Base class for entities that require audit information.
 * <p>
 * Provides fields for tracking creation and update timestamps.
 * Automatically sets these fields using the {@link AuditListener}.
 * </p>
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditListener.class)
public class AuditEntity {

    /**
     * Sort order for querying entities by creation date in descending order.
     */
    @Transient
    public static final Sort SORT_BY_CREATED_AT_DESC = Sort.by(Sort.Direction.DESC, "createdAt");

    /**
     * The timestamp when the entity was created.
     * <p>
     * Automatically set before persisting the entity.
     * </p>
     */
    @Column(nullable = false, updatable = false)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * The timestamp when the entity was last updated.
     * <p>
     * Automatically updated before updating the entity.
     * </p>
     */
    @Column(nullable = false)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

}
