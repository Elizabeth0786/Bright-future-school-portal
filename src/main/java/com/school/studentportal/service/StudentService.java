package com.school.studentportal.service;

import com.school.studentportal.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    Student saveStudent(Student student, MultipartFile photo);

    List<Student> getAllStudents();

    Page<Student> getStudentsPaginated(Pageable pageable);

    Optional<Student> getStudentById(Long id);

    Optional<Student> getStudentByStudentId(String studentId);

    Optional<Student> getStudentByAdmissionNumber(String admissionNumber);

    Student updateStudent(Long id, Student studentDetails, MultipartFile photo);

    void deleteStudent(Long id);

    List<Student> searchStudents(String query);

    List<Student> findByClass(String className);

    List<Student> findByParentPhone(String parentPhone);

    String generateStudentId();

    String generateAdmissionNumber();

    long getTotalStudents();

    long getMaleStudentsCount();

    long getFemaleStudentsCount();

    List<Object[]> getStudentsByClass();

    List<Student> getRecentStudents(int limit);
}