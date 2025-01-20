package com.lecture.lecture.Controller;

import com.lecture.lecture.Repository.DepartmentRepository;
import com.lecture.lecture.Service.StudentService;
import com.lecture.lecture.model.Department;
import com.lecture.lecture.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;
import java.util.List;

@Controller
public class RegistrationController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private DepartmentRepository departmentRepository;

    // 회원가입 페이지 표시
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("student", new Student());
        List<Department> departments = departmentRepository.findAll();
        model.addAttribute("departments", departments);
        return "register"; // Thymeleaf 템플릿 이름
    }

    // 회원가입 처리
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("student") Student student, Model model) {
        try {
            // 기본 역할 설정
            student.setRoles(Collections.singleton("ROLE_STUDENT"));
            studentService.registerStudent(student);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            List<Department> departments = departmentRepository.findAll();
            model.addAttribute("departments", departments);
            return "register";
        }
    }
}