package com.outseer.tms.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Table(name =  "user_info")
@Data
@Entity
public class UserEntity {
    @Id
    private String userId;
    private String name;
    private String email;
    private LocalDateTime createdAt;
}
