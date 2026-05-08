package com.feasymenu.server.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = java.util.UUID.class))
public abstract class AbstractEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    private Long version;

    @Column(name = "created_time")
    private Instant createdTime;

    @Column(name = "updated_time")
    private Instant updatedTime;

    @PrePersist
    private void onCreate() {
        Instant now = Instant.now();
        this.createdTime = now;
        this.updatedTime = now;
        if (this.version == null)
            this.version = 0L;
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedTime = Instant.now();
        if (this.version == null)
            this.version = 0L;
    }
}
