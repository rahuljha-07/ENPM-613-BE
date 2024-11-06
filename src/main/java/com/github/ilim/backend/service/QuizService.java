package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.QuizDto;
import com.github.ilim.backend.entity.CourseModuleItem;
import com.github.ilim.backend.entity.Question;
import com.github.ilim.backend.entity.QuestionOption;
import com.github.ilim.backend.entity.Quiz;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.exception.exceptions.CantDeleteAttemptedQuizException;
import com.github.ilim.backend.exception.exceptions.NotCourseInstructorException;
import com.github.ilim.backend.exception.exceptions.QuizNotFoundException;
import com.github.ilim.backend.repo.QuestionOptionRepo;
import com.github.ilim.backend.repo.QuestionRepo;
import com.github.ilim.backend.repo.QuizAttemptRepo;
import com.github.ilim.backend.repo.QuizRepo;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final CourseService courseService;
    private final ModuleService moduleService;

    private final QuizRepo quizRepo;
    private final QuestionRepo questionRepo;
    private final QuestionOptionRepo questionOptionRepo;
    private final QuizAttemptRepo quizAttemptRepo;
    private final ModuleItemService moduleItemService;

    public Quiz findQuizById(User user, UUID quizId) {
        var quiz = quizRepo.findById(quizId)
            .orElseThrow(() -> new QuizNotFoundException(quizId));
        var course = quiz.getCourseModule().getCourse();
        courseService.assertUserHasAccessToCourseContent(user, course);
        return quiz;
    }

    public Quiz findQuizByIdAsInstructor(User instructor, UUID quizId) {
        var quiz = findQuizById(instructor, quizId);
        if (!quiz.getCourseModule().getCourse().getInstructor().equals(instructor)) {
            throw new NotCourseInstructorException(instructor, quiz);
        }
        return quiz;
    }

    public QuizDto getQuizDtoByQuizId(User instructor, UUID quizId) {
        var quiz = findQuizById(instructor, quizId);
        return QuizDto.from(quiz, instructor.getRole());
    }

    @Transactional
    public void addQuizToModule(User instructor, UUID moduleId, QuizDto dto) {
        var module = moduleService.findModuleByIdAsInstructor(instructor, moduleId);

        // create quiz record
        var quiz = Quiz.from(dto);
        quiz.setCourseModule(module);
        quizRepo.save(quiz);

        saveQuestionsAndOptions(dto, quiz);

        // add it to module
        var moduleItem = CourseModuleItem.create(quiz);
        module.addModuleItem(moduleItem);
        moduleService.saveModule(module);
    }

    @Transactional
    public void updateQuiz(User instructor, UUID quizId, @Valid QuizDto dto) {
        var quiz = findQuizByIdAsInstructor(instructor, quizId);
        quiz.getQuestions().clear();
        quiz.updateFrom(dto);
        quizRepo.save(quiz);
        saveQuestionsAndOptions(dto, quiz);
    }

    @Transactional
    public void removeQuizFromModule(User instructor, UUID quizId) {
        var quiz = findQuizByIdAsInstructor(instructor, quizId);
        var module = quiz.getCourseModule();
        var quizAttempts = quizAttemptRepo.findQuizAttemptsByQuiz(quiz);
        if (!quizAttempts.isEmpty()) {
            throw new CantDeleteAttemptedQuizException(instructor.getId(), quiz.getId(), quizAttempts.getFirst().getId());
        }

        // delete ModuleItem record
        var moduleItem = moduleItemService.findModuleItemByQuiz(quiz);
        module.removeModuleItem(moduleItem);

        // delete Quiz record
        quizRepo.delete(quiz);
    }

    private void saveQuestionsAndOptions(QuizDto dto, Quiz quiz) {
        // Map questions and options
        int questionOrderIndex = 0;
        for (var questionDto : dto.getQuestions()) {
            var question = Question.from(questionDto);
            question.setQuiz(quiz);
            question.setOrderIndex(questionOrderIndex++);
            questionRepo.save(question);

            // Map options
            int optionOrderIndex = 0;
            for (var optionDto : questionDto.getOptions()) {
                var option = QuestionOption.from(optionDto);
                option.setQuestion(question);
                option.setOrderIndex(optionOrderIndex++);
                questionOptionRepo.save(option);
            }
        }
    }
}
