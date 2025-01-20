package com.lecture.lecture.Repository;

import com.lecture.lecture.model.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassEntityRepository extends JpaRepository<ClassEntity, Integer> {
    List<ClassEntity> findByCourse_CourseId(Integer courseId);
}