package com.example.sms.controller;

import com.example.sms.domain.Student;
import com.example.sms.dto.ResponseWrapper;
import com.example.sms.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/students")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public ResponseWrapper<List<Student>> getStudentList() {
        List<Student> studentList = studentService.getStudentList();
        return ResponseWrapper.<List<Student>>builder().result(studentList).success(true).build();
    }

    @PostMapping
    public ResponseWrapper<Student> createStudent(@RequestBody Student student) {
        Student studentCreated = studentService.createStudent(student);
        return ResponseWrapper.<Student>builder().result(studentCreated).success(true).build();
    }

    @GetMapping("/{id}")
    public ResponseWrapper<Student> getStudent(@PathVariable long id) {
        Student studentById = studentService.getStudentById(id);
        return ResponseWrapper.<Student>builder().result(studentById).success(true).build();
    }


}
