package com.github.ilim.backend.controller;

import com.github.ilim.backend.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

}
