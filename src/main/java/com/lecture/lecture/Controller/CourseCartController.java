// src/main/java/com/lecture/lecture/Controller/CourseCartController.java
package com.lecture.lecture.Controller;

import com.lecture.lecture.Repository.ClassEntityRepository;
import com.lecture.lecture.Repository.CourseRepository;
import com.lecture.lecture.Repository.StudentRepository;
import com.lecture.lecture.Service.CourseCartService;
import com.lecture.lecture.model.ClassEntity;
import com.lecture.lecture.model.Course;
import com.lecture.lecture.model.CourseCart;
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
@RequestMapping("/cart")
public class CourseCartController {

    private static final Logger logger = LoggerFactory.getLogger(CourseCartController.class);

    @Autowired
    private CourseCartService courseCartService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ClassEntityRepository classEntityRepository; // 추가

    // 장바구니 추가
    @PostMapping("/add")
    public Map<String, Object> addToCart(@RequestParam("courseId") Integer courseId,
                                         @RequestParam("classId") Integer classId,
                                         Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        String email = authentication.getName();
        logger.info("User {} attempting to add course {} class {} to cart", email, courseId, classId);
        Optional<Student> optionalStudent = studentRepository.findByEmail(email);
        if (optionalStudent.isEmpty()) {
            logger.warn("User {} is unauthenticated.", email);
            response.put("status", "unauthenticated");
            return response;
        }
        Student student = optionalStudent.get();

        Optional<Course> optionalCourse = courseRepository.findById(courseId);
        if (optionalCourse.isEmpty()) {
            logger.error("Course {} not found for user {}", courseId, email);
            response.put("status", "error");
            response.put("message", "존재하지 않는 강의입니다.");
            return response;
        }
        Course course = optionalCourse.get();

        Optional<ClassEntity> optionalClass = classEntityRepository.findById(classId);
        if (optionalClass.isEmpty()) {
            logger.error("ClassEntity {} not found for user {}", classId, email);
            response.put("status", "error");
            response.put("message", "존재하지 않는 클래스입니다.");
            return response;
        }
        ClassEntity classEntity = optionalClass.get();

        try {
            CourseCart cart = courseCartService.addToCart(student, course, classEntity);
            response.put("status", "success");


            Map<String, Object> cartData = new HashMap<>();
            cartData.put("cartId", cart.getCartId());
            cartData.put("courseId", cart.getCourse().getCourseId());
            cartData.put("courseName", cart.getCourse().getCourseName());
            cartData.put("departmentName", cart.getCourse().getDepartment().getDepartmentName());
            cartData.put("classification", cart.getCourse().getClassification());
            cartData.put("semester", cart.getCourse().getSemester());
            cartData.put("credit", cart.getCourse().getCredit());
            cartData.put("professorName", cart.getClassEntity().getProfessor().getName());
            cartData.put("roomNo", cart.getClassEntity().getRoomNo());
            cartData.put("dayOfWeek", cart.getClassEntity().getDayOfWeek());
            cartData.put("startTime", cart.getClassEntity().getStartTime());
            cartData.put("endTime", cart.getClassEntity().getEndTime());
            cartData.put("classId", cart.getClassEntity().getClassId());

            response.put("cart", cartData);

            logger.info("User {} successfully added course {} class {} to cart", email, courseId, classId);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            logger.error("Failed to add course {} class {} to cart for user {}: {}", courseId, classId, email, e.getMessage());
        }

        return response;
    }


    // 장바구니 삭제
    @PostMapping("/remove")
    public Map<String, Object> removeFromCart(@RequestParam("cartId") Integer cartId,
                                              Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        String email = authentication.getName();
        logger.info("User {} attempting to remove cart item {}", email, cartId);
        Optional<Student> optionalStudent = studentRepository.findByEmail(email);
        if (optionalStudent.isEmpty()) {
            logger.warn("User {} is unauthenticated.", email);
            response.put("status", "unauthenticated");
            return response;
        }
        Student student = optionalStudent.get();

        try {
            courseCartService.removeFromCart(cartId, student);
            response.put("status", "success");
            logger.info("User {} successfully removed cart item {}", email, cartId);
            // 필요한 경우 현재 학점이나 기타 데이터 포함
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            logger.error("Failed to remove cart item {} for user {}: {}", cartId, email, e.getMessage());
        }

        return response;
    }
}