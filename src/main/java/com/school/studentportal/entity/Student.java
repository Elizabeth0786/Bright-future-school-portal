package com.school.studentportal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String studentId;

    @Column(unique = true, nullable = false)
    private String admissionNumber;

    @NotBlank(message = "Surname is required")
    private String surname;

    @NotBlank(message = "First name is required")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    private Integer age;

    @NotBlank(message = "Class is required")
    private String className;

    @NotBlank(message = "Parent name is required")
    private String parentName;

    @NotBlank(message = "Parent phone is required")
    private String parentPhone;

    @Email(message = "Invalid email format")
    private String parentEmail;

    @NotBlank(message = "Address is required")
    private String address;

    private String state;
    private String nationality;
    private String religion;
    private String bloodGroup;
    private String medicalCondition;

    @NotBlank(message = "Emergency contact is required")
    private String emergencyContact;

    private LocalDate dateAdmitted;
    private String status;
    private String photo;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "Active";
        }
        if (this.dateAdmitted == null) {
            this.dateAdmitted = LocalDate.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public String getFullName() {
        StringBuilder fullName = new StringBuilder();

        if (surname != null && !surname.isBlank()) {
            fullName.append(surname);
        }

        if (firstName != null && !firstName.isBlank()) {
            if (fullName.length() > 0) {
                fullName.append(" ");
            }
            fullName.append(firstName);
        }

        if (middleName != null && !middleName.isBlank()) {
            if (fullName.length() > 0) {
                fullName.append(" ");
            }
            fullName.append(middleName);
        }

        return fullName.toString();
    }
}
