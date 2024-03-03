package com.example.sms.service;

import com.example.sms.domain.Student;
import com.example.sms.exception.ErrorEnum;
import com.example.sms.exception.SmsException;
import com.example.sms.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public List<Student> getStudentList() {
        return studentRepository.findAll();
    }

    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student getStudentById(long id) {
        Optional<Student> byId = studentRepository.findById(id);
        if (byId.isEmpty()) {
            throw new SmsException(ErrorEnum.INVALID_STUDENT_ID);
        }
        return byId.get();
    }
}
