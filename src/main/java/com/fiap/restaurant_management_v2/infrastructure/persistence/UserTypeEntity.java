package com.fiap.restaurant_management_v2.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Builder
@Entity
@Table(name = "user_type")
@BatchSize(size = 50)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserTypeEntity {

    @Id
    private UUID id;

    @Column(nullable = false , name = "user_type")
    private String userType;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updated_at", nullable = true)
    @UpdateTimestamp
    private Instant updatedAt;

    @Column(name = "deleted_at", nullable = true)
    private Instant deletedAt;


}
