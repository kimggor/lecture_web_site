// src/main/java/com/lecture/lecture/dto/EnrollmentDTO.java
package com.lecture.lecture.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentDTO {
    private Integer enrollmentId;
    private String courseName;
    private String departmentName;
    private String classification;
    private String semester;
    private Integer credit;
    private String professorName;
    private String roomNo;
    private String time; // "요일 (시작시간-종료시간)"


}
