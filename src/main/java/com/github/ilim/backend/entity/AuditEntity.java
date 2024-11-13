package com.github.ilim.backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.ilim.backend.util.AuditListener;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
@EntityListeners(AuditListener.class)
public class AuditEntity {

    @Column(nullable = false, updatable = false)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

}