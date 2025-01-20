package com.lecture.lecture.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "Course_History", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "class_id", "course_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_history_id")
    private Integer courseHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, length = 10)
    private String GPA;

    @Column(name = "is_retake", nullable = false)
    private Boolean isRetake;
}
