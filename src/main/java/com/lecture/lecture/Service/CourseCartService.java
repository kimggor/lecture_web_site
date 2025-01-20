// src/main/java/com/lecture/lecture/Service/CourseCartService.java
package com.lecture.lecture.Service;

import com.lecture.lecture.Repository.CourseCartRepository;
import com.lecture.lecture.model.ClassEntity;
import com.lecture.lecture.model.Course;
import com.lecture.lecture.model.CourseCart;
import com.lecture.lecture.model.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseCartService {

    private static final Logger logger = LoggerFactory.getLogger(CourseCartService.class);

    @Autowired
    private CourseCartRepository courseCartRepository;

    @Autowired
    private EnrollmentService enrollmentService;

    /**
     * 장바구니에 추가
     *
     * @return
     */
    public CourseCart addToCart(Student student, Course course, ClassEntity classEntity) throws Exception {
        // 이미 장바구니에 추가된 강의인지 확인
        Optional<CourseCart> existingCart = courseCartRepository.findByStudentAndCourseAndClassEntity(student, course, classEntity);
        if (existingCart.isPresent()) {
            logger.warn("Course {} with class {} is already in cart for student {}", course.getCourseId(), classEntity.getClassId(), student.getStudentId());
            throw new Exception("이미 장바구니에 추가된 강의입니다.");
        }

        // 수강신청 여부 확인
        boolean isEnrolled = enrollmentService.isAlreadyEnrolled(student, course.getCourseId());
        if (isEnrolled) {
            logger.warn("Student {} is already enrolled in course {}", student.getStudentId(), course.getCourseId());
            throw new Exception("이미 수강신청된 강의입니다.");
        }

        // 장바구니에 추가
        CourseCart cart = new CourseCart();
        cart.setStudent(student);
        cart.setCourse(course);
        cart.setClassEntity(classEntity);
        courseCartRepository.save(cart);
        logger.info("Added course {} with class {} to cart for student {}", course.getCourseId(), classEntity.getClassId(), student.getStudentId());
        return cart;
    }

    /**
     * 장바구니에서 삭제
     */
    public void removeFromCart(Integer cartId, Student student) throws Exception {
        CourseCart cart = courseCartRepository.findById(cartId)
                .orElseThrow(() -> new Exception("장바구니 항목을 찾을 수 없습니다."));

        if (!cart.getStudent().equals(student)) {
            logger.warn("Student {} is not authorized to remove cart item {}", student.getStudentId(), cartId);
            throw new Exception("권한이 없습니다.");
        }

        courseCartRepository.delete(cart);
        logger.info("Removed cart item {} from cart for student {}", cartId, student.getStudentId());
    }

    public List<CourseCart> getCourseCartsByStudent(Student student) {
        return courseCartRepository.findByStudent(student);
    }
}
