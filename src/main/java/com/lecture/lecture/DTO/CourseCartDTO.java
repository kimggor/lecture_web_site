// src/main/java/com/lecture/lecture/dto/CourseCartDTO.java
package com.lecture.lecture.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseCartDTO {
    private Integer cartId;
    private Integer courseId;
    private String courseName;
    private String departmentName;
    private String classification;
    private String semester;
    private Integer credit;
    private String professorName;
    private String roomNo;
    private String time; // "요일 (시작시간-종료시간)"


    public CourseCartDTO(Integer cartId, Integer courseId, Integer classId, String courseName, String departmentName, String classification, String semester, Integer credit, String name, String roomNo, String time, String addedDate) {
    }


}
