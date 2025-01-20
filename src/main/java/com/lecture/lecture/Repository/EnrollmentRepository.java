package com.lecture.lecture.Repository;

import com.lecture.lecture.model.ClassEntity;
import com.lecture.lecture.model.Course;
import com.lecture.lecture.model.Enrollment;
import com.lecture.lecture.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    Optional<Enrollment> findByStudentAndClassEntity(Student student, ClassEntity classEntity);

    List<Enrollment> findByStudent(Student student);

    Optional<Enrollment> findByStudentAndCourse(Student student, Course course);

    boolean existsByStudentAndCourse_CourseId(Student student, int courseId);
}