package com.github.ilim.backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.ilim.backend.util.AuditListener;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
@EntityListeners(AuditListener.class)
public class AuditEntity {

    @Transient
    public static final Sort SORT_BY_CREATED_AT_DESC = Sort.by(Sort.Direction.DESC, "createdAt");

    @Column(nullable = false, updatable = false)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

}