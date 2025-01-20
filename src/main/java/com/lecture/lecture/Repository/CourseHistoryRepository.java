package com.lecture.lecture.Repository;

import com.lecture.lecture.model.CourseHistory;
import com.lecture.lecture.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseHistoryRepository extends JpaRepository<CourseHistory, Integer> {
    List<CourseHistory> findByStudent(Student student);
}