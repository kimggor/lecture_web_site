// src/main/java/com/lecture/lecture/dto/CourseDTO.java
package com.lecture.lecture.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Integer courseId;
    private String courseName;
    private String classification;
    private Integer credit;
    private String departmentName;
    private String semester;
    private List<ClassDTO> classes;


}
