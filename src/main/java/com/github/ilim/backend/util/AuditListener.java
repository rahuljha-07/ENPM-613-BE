package com.github.ilim.backend.util;

import com.github.ilim.backend.entity.AuditEntity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;

public class AuditListener {

    @PrePersist
    public void setCreatedAt(AuditEntity audit) {
        audit.setCreatedAt(LocalDateTime.now());
        audit.setUpdatedAt(LocalDateTime.now());
    }

    @PreUpdate
    public void setUpdatedAt(AuditEntity audit) {
        audit.setUpdatedAt(LocalDateTime.now());
    }
}
