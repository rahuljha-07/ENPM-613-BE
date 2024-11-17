package com.github.ilim.backend.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AuditEntityTest {

    @Test
    void testSettersAndGetters() {
        AuditEntity auditEntity = new AuditEntity();
        LocalDateTime now = LocalDateTime.now();

        auditEntity.setCreatedAt(now);
        auditEntity.setUpdatedAt(now);

        assertEquals(now, auditEntity.getCreatedAt());
        assertEquals(now, auditEntity.getUpdatedAt());
    }
}
