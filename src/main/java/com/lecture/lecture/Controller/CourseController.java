// src/main/java/com/lecture/lecture/Controller/CourseController.java
package com.lecture.lecture.Controller;

import com.lecture.lecture.Repository.StudentRepository;
import com.lecture.lecture.Service.CourseCartService;
import com.lecture.lecture.Service.CourseService;
import com.lecture.lecture.Service.EnrollmentService;
import com.lecture.lecture.model.Course;
import com.lecture.lecture.model.CourseCart;
import com.lecture.lecture.model.Enrollment;
import com.lecture.lecture.model.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseCartService courseCartService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private StudentRepository studentRepository;

    /**
     * 수강신청 페이지 표시
     */
    @GetMapping("/enrollment")
    public String viewEnrollment(Model model, Principal principal) {
        String email = principal.getName();
        logger.info("Accessing enrollment page for user: {}", email);
        Optional<Student> optionalStudent = studentRepository.findByEmail(email);
        if (optionalStudent.isEmpty()) {
            logger.warn("User not authenticated: {}", email);
            return "redirect:/login";
        }
        Student student = optionalStudent.get();

        List<Course> allCourses = courseService.getAllCourses();

        // 학과 필터링
        Map<Integer, String> departmentsMap = courseService.getDepartmentsMap();
        int currentCredits = enrollmentService.getCurrentCredits(student);

        List<CourseCart> courseCarts = courseCartService.getCourseCartsByStudent(student);
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(student); // 신청된 강의 목록

        // 신청된 강의 ID 목록 추출
        List<Integer> enrolledCourseIds = enrollments.stream()
                .map(enrollment -> enrollment.getCourse().getCourseId())
                .collect(Collectors.toList());

        // 장바구니에 담긴 강의 ID 목록 추출
        List<Integer> cartCourseIds = courseCarts.stream()
                .map(cart -> cart.getCourse().getCourseId())
                .collect(Collectors.toList());

        model.addAttribute("allCourses", allCourses);
        model.addAttribute("departmentsMap", departmentsMap);
        model.addAttribute("currentCredits", currentCredits);
        model.addAttribute("courseCarts", courseCarts);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("enrolledCourseIds", enrolledCourseIds);
        model.addAttribute("cartCourseIds", cartCourseIds);

        logger.info("Enrollment page loaded for user: {}", email);
        return "enrollment";
    }

    /**
     * 장바구니 페이지 표시
     */
    @GetMapping("/cart")
    public String viewCart(Model model, Principal principal) {
        String email = principal.getName();
        logger.info("Accessing cart page for user: {}", email);
        Optional<Student> optionalStudent = studentRepository.findByEmail(email);
        if (optionalStudent.isEmpty()) {
            logger.warn("User not authenticated: {}", email);
            return "redirect:/login";
        }
        Student student = optionalStudent.get();

        List<CourseCart> courseCarts = courseCartService.getCourseCartsByStudent(student);
        List<Course> allCourses = courseService.getAllCourses();
        Map<Integer, String> departmentsMap = courseService.getDepartmentsMap();
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(student);
        List<Integer> enrolledCourseIds = enrollments.stream()
                .map(enrollment -> enrollment.getCourse().getCourseId())
                .collect(Collectors.toList());

        // 장바구니에 담긴 강의 ID 목록 추출
        List<Integer> cartCourseIds = courseCarts.stream()
                .map(cart -> cart.getCourse().getCourseId())
                .collect(Collectors.toList());

        model.addAttribute("courseCarts", courseCarts);
        model.addAttribute("allCourses", allCourses);
        model.addAttribute("departmentsMap", departmentsMap);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("enrolledCourseIds", enrolledCourseIds);
        model.addAttribute("cartCourseIds", cartCourseIds);

        logger.info("Cart page loaded for user: {}", email);
        return "cart";
    }
}
