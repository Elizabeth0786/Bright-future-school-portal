package com.school.studentportal.repository;

import com.school.studentportal.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByStudentId(String studentId);

    Optional<Student> findByAdmissionNumber(String admissionNumber);

    List<Student> findBySurnameContainingIgnoreCase(String surname);

    List<Student> findByClassName(String className);

    List<Student> findByParentPhone(String parentPhone);

    @Query("SELECT s FROM Student s WHERE " +
            "LOWER(s.surname) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.middleName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.studentId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.admissionNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.className) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.parentName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.parentPhone) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Student> searchStudents(@Param("query") String query);

    long countByGender(String gender);

    @Query("SELECT s.className, COUNT(s) FROM Student s GROUP BY s.className ORDER BY COUNT(s) DESC")
    List<Object[]> countByClass();

    Page<Student> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT s FROM Student s ORDER BY s.createdAt DESC")
    List<Student> findRecentStudents(Pageable pageable);
}