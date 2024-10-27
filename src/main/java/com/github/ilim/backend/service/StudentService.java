package com.github.ilim.backend.service;

import com.github.ilim.backend.entity.InstructorApplication;
import com.github.ilim.backend.exception.exceptions.InstructorAppNotFoundException;
import com.github.ilim.backend.exception.exceptions.UserNotFoundException;
import com.github.ilim.backend.repo.InstructorApplicationRepo;
import com.github.ilim.backend.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserRepo userRepo;

}
