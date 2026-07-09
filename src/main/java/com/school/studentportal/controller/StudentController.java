package com.school.studentportal.controller;

import com.school.studentportal.entity.Student;
import com.school.studentportal.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            model.addAttribute("totalStudents", studentService.getTotalStudents());
            model.addAttribute("maleStudents", studentService.getMaleStudentsCount());
            model.addAttribute("femaleStudents", studentService.getFemaleStudentsCount());

            List<Object[]> studentsByClass = studentService.getStudentsByClass();
            model.addAttribute("studentsByClass", studentsByClass != null ? studentsByClass : new ArrayList<>());

            List<Student> recentStudents = studentService.getRecentStudents(5);
            model.addAttribute("recentStudents", recentStudents != null ? recentStudents : new ArrayList<>());
        } catch (Exception e) {
            // If no students exist yet, provide defaults
            model.addAttribute("totalStudents", 0L);
            model.addAttribute("maleStudents", 0L);
            model.addAttribute("femaleStudents", 0L);
            model.addAttribute("studentsByClass", new ArrayList<>());
            model.addAttribute("recentStudents", new ArrayList<>());
        }

        return "admin/dashboard";
    }

    // Show registration form
    @GetMapping("/students/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("pageTitle", "Register New Student");
        return "admin/register-student";
    }

    // Process registration
    @PostMapping("/students/register")
    public String registerStudent(
            @Valid @ModelAttribute("student") Student student,
            BindingResult result,
            @RequestParam(value = "photoFile", required = false) MultipartFile photo,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Register New Student");
            return "admin/register-student";
        }

        try {
            Student savedStudent = studentService.saveStudent(student, photo);
            redirectAttributes.addFlashAttribute("success",
                    "Student registered successfully! Student ID: " + savedStudent.getStudentId());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error registering student: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/admin/students";
    }

    // List all students
    @GetMapping("/students")
    public String listStudents(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        try {
            if (search != null && !search.trim().isEmpty()) {
                List<Student> searchResults = studentService.searchStudents(search.trim());
                model.addAttribute("students", searchResults);
                model.addAttribute("searchQuery", search);
                model.addAttribute("isSearchResult", true);
            } else {
                Page<Student> studentPage = studentService.getStudentsPaginated(
                        PageRequest.of(page, size, Sort.by("createdAt").descending()));

                model.addAttribute("students", studentPage.getContent());
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", studentPage.getTotalPages());
                model.addAttribute("totalItems", studentPage.getTotalElements());
                model.addAttribute("pageSize", size);
                model.addAttribute("isSearchResult", false);
            }
        } catch (Exception e) {
            model.addAttribute("students", new ArrayList<>());
            model.addAttribute("error", "Error loading students: " + e.getMessage());
        }

        model.addAttribute("pageTitle", "Student List");
        return "admin/students";
    }

    // View student profile
    @GetMapping("/students/view/{id}")
    public String viewStudent(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Student> studentOpt = studentService.getStudentById(id);
            if (studentOpt.isPresent()) {
                model.addAttribute("student", studentOpt.get());
                model.addAttribute("pageTitle", "Student Profile");
                return "admin/student-profile";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error loading student profile");
        }
        return "redirect:/admin/students";
    }

    // Show edit form
    @GetMapping("/students/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Student> studentOpt = studentService.getStudentById(id);
            if (studentOpt.isPresent()) {
                model.addAttribute("student", studentOpt.get());
                model.addAttribute("pageTitle", "Edit Student");
                return "admin/edit-student";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Student not found");
        }
        return "redirect:/admin/students";
    }

    // Process update
    @PostMapping("/students/update/{id}")
    public String updateStudent(
            @PathVariable Long id,
            @Valid @ModelAttribute("student") Student student,
            BindingResult result,
            @RequestParam(value = "photoFile", required = false) MultipartFile photo,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Edit Student");
            return "admin/edit-student";
        }

        try {
            studentService.updateStudent(id, student, photo);
            redirectAttributes.addFlashAttribute("success", "Student updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating student: " + e.getMessage());
        }

        return "redirect:/admin/students";
    }

    // Delete student
    @GetMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("success", "Student deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting student: " + e.getMessage());
        }
        return "redirect:/admin/students";
    }

    // Search students
    @GetMapping("/students/search")
    public String searchStudents(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String searchType,
            Model model) {

        try {
            List<Student> results = new ArrayList<>();

            if (query != null && !query.trim().isEmpty()) {
                results = studentService.searchStudents(query.trim());
            } else {
                results = studentService.getAllStudents();
            }

            model.addAttribute("students", results);
            model.addAttribute("searchQuery", query);
            model.addAttribute("isSearchResult", true);
        } catch (Exception e) {
            model.addAttribute("students", new ArrayList<>());
            model.addAttribute("error", "Search error: " + e.getMessage());
        }

        model.addAttribute("pageTitle", "Search Results");
        return "admin/students";
    }
}