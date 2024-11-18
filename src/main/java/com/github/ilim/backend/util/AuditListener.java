package com.github.ilim.backend.util;

import com.github.ilim.backend.entity.AuditEntity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;

/**
 * Entity listener for automatically setting audit fields on persist and update operations.
 * <p>
 * Sets the {@code createdAt} and {@code updatedAt} timestamps before persisting a new entity,
 * and updates the {@code updatedAt} timestamp before updating an existing entity.
 * </p>
 */
public class AuditListener {

    /**
     * Sets the {@code createdAt} and {@code updatedAt} fields before persisting a new entity.
     *
     * @param audit the {@link AuditEntity} being persisted
     */
    @PrePersist
    public void setCreatedAt(AuditEntity audit) {
        audit.setCreatedAt(LocalDateTime.now());
        audit.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Sets the {@code updatedAt} field before updating an existing entity.
     *
     * @param audit the {@link AuditEntity} being updated
     */
    @PreUpdate
    public void setUpdatedAt(AuditEntity audit) {
        audit.setUpdatedAt(LocalDateTime.now());
    }
}
