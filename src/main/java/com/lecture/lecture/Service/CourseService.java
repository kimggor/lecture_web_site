// src/main/java/com/lecture/lecture/Service/CourseService.java
package com.lecture.lecture.Service;

import com.lecture.lecture.Repository.ClassEntityRepository;
import com.lecture.lecture.Repository.CourseRepository;
import com.lecture.lecture.Repository.DepartmentRepository;
import com.lecture.lecture.model.ClassEntity;
import com.lecture.lecture.model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ClassEntityRepository classEntityRepository;

    // 모든 강의 조회
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // 특정 학과의 강의 조회
    public List<Course> getCoursesByDepartment(Integer departmentId) {
        return courseRepository.findByDepartment_DepartmentId(departmentId);
    }

    // Department Map 로드
    public Map<Integer, String> getDepartmentsMap() {
        List<Course> courses = getAllCourses();
        return courses.stream()
                .map(course -> course.getDepartment())
                .distinct()
                .collect(Collectors.toMap(
                        dept -> dept.getDepartmentId(),
                        dept -> dept.getDepartmentName()
                ));
    }

    // 특정 Class 조회
    public Optional<ClassEntity> getClassById(Integer classId) {
        // ClassEntityRepository를 사용하여 조회
        return classEntityRepository.findById(classId);
    }

    // 기타 강의 관련 비즈니스 로직
}