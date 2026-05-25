package com.example.waterbill.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "api_logs")
public class ApiLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clientName;
    private String role;

    private String endpoint;
    private String method;
    private String status;

    private String errorMessage;
    private Integer httpStatus;

    private LocalDateTime timestamp;

    @Column(columnDefinition = "LONGTEXT")
    private String requestBody;

    @Column(columnDefinition = "LONGTEXT")
    private String responseBody;

}