package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.QuizDto;
import com.github.ilim.backend.entity.CourseModuleItem;
import com.github.ilim.backend.entity.Quiz;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.exception.exceptions.NotCourseInstructorException;
import com.github.ilim.backend.exception.exceptions.VideoNotFoundException;
import com.github.ilim.backend.repo.QuizRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepo quizRepo;
    private final CourseService courseService;
    private final ModuleService moduleService;

    public Quiz findQuizByIdAsInstructor(User instructor, UUID quizId) {
        var quiz = findQuizById(instructor, quizId);
        if (!quiz.getCourseModule().getCourse().getInstructor().equals(instructor)) {
            throw new NotCourseInstructorException(instructor, quiz);
        }
        return quiz;
    }

    public Quiz findQuizById(User instructor, UUID quizId) {
        var quiz = quizRepo.findById(quizId)
            .orElseThrow(() -> new VideoNotFoundException(quizId));
        var course = quiz.getCourseModule().getCourse();
        courseService.assertUserHasAccessToCourseContent(instructor, course);
        return quiz;
    }

    public void addQuizToModule(User instructor, UUID quizId, QuizDto dto) {
        var module = moduleService.findModuleByIdAsInstructor(instructor, quizId);

        // create quiz record
        var quiz = Quiz.from(dto);
        quiz.setCourseModule(module);
        quizRepo.save(quiz);

        // add it to module
        var moduleItem = CourseModuleItem.create(quiz);
        module.addModuleItem(moduleItem);
        moduleService.saveModule(module);
    }

    public void updateQuiz(User instructor, UUID quizId, @Valid QuizDto dto) {
        var quiz = findQuizByIdAsInstructor(instructor, quizId);
        quiz.updateFrom(dto);
        quizRepo.save(quiz);
    }

    public void removeQuizFromModule(User instructor, UUID quizId) {
        var quiz = findQuizByIdAsInstructor(instructor, quizId);
        var module = quiz.getCourseModule();
        var moduleItem = module.findModuleItemByQuizId(quizId);
        module.removeModuleItem(moduleItem);
        quizRepo.delete(quiz);
    }
}
