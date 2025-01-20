// src/main/java/com/lecture/lecture/dto/ClassDTO.java
package com.lecture.lecture.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassDTO {
    private Integer classId;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String professorName;
    private String roomNo;
    private Integer capacity;
    private Integer enrolled;


}
