// src/main/java/com/lecture/lecture/Service/EnrollmentService.java
package com.lecture.lecture.Service;

import com.lecture.lecture.Repository.*;
import com.lecture.lecture.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService {

    private static final Logger logger = LoggerFactory.getLogger(EnrollmentService.class);

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseCartRepository courseCartRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ClassEntityRepository classEntityRepository;

    /**
     * 학생의 현재 수강신청 목록 조회
     */
    public List<Enrollment> getEnrollmentsByStudent(Student student) {
        return enrollmentRepository.findByStudent(student);
    }

    /**
     * 학생의 현재 장바구니 목록 조회
     */
    public List<CourseCart> getCourseCartsByStudent(Student student) {
        return courseCartRepository.findByStudent(student);
    }

    /**
     * 현재 수강 학점 계산
     */
    public int getCurrentCredits(Student student) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudent(student);
        return enrollments.stream()
                .mapToInt(enrollment -> enrollment.getCourse().getCredit())
                .sum();
    }

    /**
     * 특정 Course에 이미 등록되어 있는지 확인
     */
    public boolean isAlreadyEnrolled(Student student, int courseId) {
        return enrollmentRepository.existsByStudentAndCourse_CourseId(student, courseId);
    }

    /**
     * 특정 Enrollment 조회
     */
    public Optional<Enrollment> getEnrollmentById(Integer enrollmentId) {
        return enrollmentRepository.findById(enrollmentId);
    }

    /**
     * 수강신청 로직
     */
    @Transactional
    public Enrollment enroll(Student student, Integer classId) throws EnrollmentException {
        logger.info("Enrolling student {} to class {}", student.getStudentId(), classId);

        // 클래스 조회
        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new EnrollmentException("클래스를 찾을 수 없습니다."));
        logger.debug("클래스 조회 완료: {}", classEntity);

        // 해당 클래스의 강의 조회
        Course course = classEntity.getCourse();
        logger.debug("강의 조회 완료: {}", course);

        // 중복 수강 체크
        if (isAlreadyEnrolled(student, course.getCourseId())) {
            logger.warn("학생 {}는 이미 강의 {}에 등록되어 있습니다.", student.getStudentId(), course.getCourseId());
            throw new EnrollmentException("이미 수강신청한 강의입니다.");
        }

        // 시간 중복 확인
        List<Enrollment> existingEnrollments = getEnrollmentsByStudent(student);
        for (Enrollment enrollment : existingEnrollments) {
            ClassEntity enrolledClass = enrollment.getClassEntity();
            if (isTimeConflict(enrolledClass, classEntity)) {
                logger.warn("시간 중복 감지: 학생 {}, 클래스 {}와 클래스 {}가 겹칩니다.",
                        student.getStudentId(), enrolledClass.getClassId(), classId);
                throw new EnrollmentException("시간이 겹치는 강의가 있습니다.");
            }
        }

        // 클래스의 수용 인원 확인
        if (classEntity.getEnrolled() >= classEntity.getCapacity()) {
            logger.warn("클래스 {}는 이미 정원이 초과되었습니다. Capacity: {}, Enrolled: {}",
                    classId, classEntity.getCapacity(), classEntity.getEnrolled());
            throw new EnrollmentException("해당 클래스의 수용 인원이 초과되었습니다.");
        }

        // 수강신청 생성
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setClassEntity(classEntity);
        enrollmentRepository.save(enrollment);
        logger.info("수강신청 생성 완료: {}", enrollment);

        // 클래스의 신청 인원 증가
        classEntity.setEnrolled(classEntity.getEnrolled() + 1);
        classEntityRepository.save(classEntity);
        logger.debug("클래스 {}의 신청 인원 증가: 현재 신청 인원 = {}", classId, classEntity.getEnrolled());

        logger.info("학생 {}가 클래스 {}에 성공적으로 등록되었습니다.", student.getStudentId(), classId);
        return enrollment;
    }

    /**
     * 수강신청 삭제 로직
     */
    @Transactional
    public void removeEnrollment(Enrollment enrollment) throws EnrollmentException {
        logger.info("Removing enrollment {} for student {}", enrollment.getEnrollmentId(), enrollment.getStudent().getStudentId());

        // 클래스 조회
        ClassEntity classEntity = enrollment.getClassEntity();
        logger.debug("클래스 조회 완료: {}", classEntity);

        // 수강신청 삭제
        enrollmentRepository.delete(enrollment);
        logger.info("수강신청 {} 삭제 완료.", enrollment.getEnrollmentId());

        // 클래스의 신청 인원 감소
        classEntity.setEnrolled(classEntity.getEnrolled() - 1);
        classEntityRepository.save(classEntity);
        logger.debug("클래스 {}의 신청 인원 감소: 현재 신청 인원 = {}", classEntity.getClassId(), classEntity.getEnrolled());

        logger.info("학생 {}의 수강신청 {}이 성공적으로 삭제되었습니다.", enrollment.getStudent().getStudentId(), enrollment.getEnrollmentId());
    }

    /**
     * 시간 중복 확인 메소드 (요일 포함)
     */
    private boolean isTimeConflict(ClassEntity enrolledClass, ClassEntity newClass) {
        if (!enrolledClass.getDayOfWeek().equals(newClass.getDayOfWeek())) {
            return false;
        }
        return enrolledClass.getStartTime().isBefore(newClass.getEndTime()) &&
                newClass.getStartTime().isBefore(enrolledClass.getEndTime());
    }
}
