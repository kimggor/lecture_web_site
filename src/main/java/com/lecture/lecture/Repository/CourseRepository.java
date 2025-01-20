package com.lecture.lecture.Repository;

import com.lecture.lecture.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    List<Course> findByDepartment_DepartmentId(Integer departmentId);

    List<Course> findByDepartment_DepartmentName(String departmentName);
    // 추가적인 쿼리 메소드 정의 가능
}