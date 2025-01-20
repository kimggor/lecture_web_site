package com.lecture.lecture.Repository;

import com.lecture.lecture.model.ClassEntity;
import com.lecture.lecture.model.Course;
import com.lecture.lecture.model.CourseCart;
import com.lecture.lecture.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseCartRepository extends JpaRepository<CourseCart, Integer> {
    Optional<CourseCart> findByStudentAndClassEntity(Student student, ClassEntity classEntity);

    Optional<CourseCart> findByStudentAndCourse(Student student, Course course);

    List<CourseCart> findByStudent(Student student);

    Optional<CourseCart> findByStudentAndCourseAndClassEntity(Student student, Course course, ClassEntity classEntity);
}