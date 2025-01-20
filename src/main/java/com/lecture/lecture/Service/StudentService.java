// src/main/java/com/lecture/lecture/service/StudentService.java
package com.lecture.lecture.Service;

import com.lecture.lecture.Repository.StudentRepository;
import com.lecture.lecture.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private PasswordEncoder passwordEncoder; // 빈으로 주입받음

    @Autowired
    private StudentRepository studentRepository;

    // 학생 인증
    public Optional<Student> authenticate(String email, String password) {
        return studentRepository.findByEmail(email)
                .filter(student -> passwordEncoder.matches(password, student.getPassword()));
    }

    // 새로운 학생 등록
    public Student registerStudent(Student student) {
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        return studentRepository.save(student);
    }

    // 기타 학생 관련 비즈니스 로직
}
