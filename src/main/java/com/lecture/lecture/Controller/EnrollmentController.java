// src/main/java/com/lecture/lecture/Controller/EnrollmentController.java
package com.lecture.lecture.Controller;

import com.lecture.lecture.Repository.StudentRepository;
import com.lecture.lecture.Service.EnrollmentException;
import com.lecture.lecture.Service.EnrollmentService;
import com.lecture.lecture.model.Enrollment;
import com.lecture.lecture.model.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/enroll")
public class EnrollmentController {

    private static final Logger logger = LoggerFactory.getLogger(EnrollmentController.class);

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private StudentRepository studentRepository;

    /**
     * 수강신청 처리 엔드포인트
     */
    @PostMapping("/add")
    public Map<String, Object> enrollCourse(@RequestParam("classId") Integer classId,
                                            Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        String email = authentication.getName();
        logger.info("User {} attempting to enroll in class {}", email, classId);
        Optional<Student> optionalStudent = studentRepository.findByEmail(email);
        if (optionalStudent.isEmpty()) {
            logger.warn("User {} is unauthenticated.", email);
            response.put("status", "unauthenticated");
            return response;
        }
        Student student = optionalStudent.get();

        try {
            Enrollment enrollment = enrollmentService.enroll(student, classId);
            int currentCredits = enrollmentService.getCurrentCredits(student);
            int updatedEnrolled = enrollment.getClassEntity().getEnrolled(); // 업데이트된 신청 인원

            response.put("status", "success");
            response.put("currentCredits", currentCredits);
            response.put("enrollmentId", enrollment.getEnrollmentId());
            response.put("courseId", enrollment.getCourse().getCourseId());
            response.put("courseName", enrollment.getCourse().getCourseName());
            response.put("departmentName", enrollment.getCourse().getDepartment().getDepartmentName());
            response.put("classification", enrollment.getCourse().getClassification());
            response.put("semester", enrollment.getCourse().getSemester());
            response.put("credit", enrollment.getCourse().getCredit());
            response.put("professorName", enrollment.getClassEntity().getProfessor().getName());
            response.put("roomNo", enrollment.getClassEntity().getRoomNo());
            response.put("time", enrollment.getClassEntity().getDayOfWeek() + " (" +
                    enrollment.getClassEntity().getStartTime() + "-" +
                    enrollment.getClassEntity().getEndTime() + ")");
            response.put("updatedEnrolled", updatedEnrolled); // 업데이트된 신청 인원 추가

            logger.info("User {} successfully enrolled in class {}. Current credits: {}",
                    email, classId, currentCredits);
        } catch (EnrollmentException e) {
            String message = e.getMessage();
            response.put("status", "error");
            response.put("message", message);
            logger.error("Enrollment failed for user {}: {}", email, message);
        }

        return response;
    }

    /**
     * 수강신청 취소 처리 엔드포인트
     */
    @PostMapping("/remove")
    public Map<String, Object> removeEnrollment(@RequestParam("enrollmentId") Integer enrollmentId,
                                                Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        String email = authentication.getName();
        logger.info("User {} attempting to remove enrollment {}", email, enrollmentId);
        Optional<Student> optionalStudent = studentRepository.findByEmail(email);
        if (optionalStudent.isEmpty()) {
            logger.warn("User {} is unauthenticated.", email);
            response.put("status", "unauthenticated");
            return response;
        }
        Student student = optionalStudent.get();

        try {
            Enrollment enrollment = enrollmentService.getEnrollmentById(enrollmentId)
                    .orElseThrow(() -> new EnrollmentException("Enrollment not found."));
            if (!enrollment.getStudent().equals(student)) {
                logger.warn("User {} is not authorized to remove enrollment {}", email, enrollmentId);
                response.put("status", "forbidden");
                response.put("message", "You are not authorized to remove this enrollment.");
                return response;
            }

            enrollmentService.removeEnrollment(enrollment);
            int currentCredits = enrollmentService.getCurrentCredits(student);
            int updatedEnrolled = enrollment.getClassEntity().getEnrolled(); // 업데이트된 신청 인원

            response.put("status", "success");
            response.put("currentCredits", currentCredits);
            response.put("courseId", enrollment.getCourse().getCourseId());
            response.put("updatedEnrolled", updatedEnrolled); // 업데이트된 신청 인원 추가

            logger.info("User {} successfully removed enrollment {}. Current credits: {}",
                    email, enrollmentId, currentCredits);
        } catch (EnrollmentException e) {
            String message = e.getMessage();
            response.put("status", "error");
            response.put("message", message);
            logger.error("Failed to remove enrollment {} for user {}: {}", enrollmentId, email, message);
        }

        return response;
    }
}
