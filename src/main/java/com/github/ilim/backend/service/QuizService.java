package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.QuizDto;
import com.github.ilim.backend.entity.AuditEntity;
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

/**
 * Service class responsible for managing quizzes within courses.
 * <p>
 * Provides functionalities such as creating, updating, retrieving, and deleting quizzes,
 * as well as managing associated questions and options.
 * </p>
 *
 * @see QuizRepo
 * @see CourseService
 * @see ModuleService
 */
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

    /**
     * Retrieves a quiz by its unique identifier, ensuring the user has access to its content.
     *
     * @param user    the {@link User} entity requesting the quiz
     * @param quizId  the unique identifier of the quiz
     * @return the {@link Quiz} entity corresponding to the provided ID
     * @throws QuizNotFoundException           if no quiz is found with the given ID
     * @throws NotCourseInstructorException    if the user does not have access to the quiz content
     */
    public Quiz findQuizById(User user, UUID quizId) {
        var quiz = quizRepo.findById(quizId)
            .orElseThrow(() -> new QuizNotFoundException(quizId));
        var course = quiz.getCourseModule().getCourse();
        courseService.assertUserHasAccessToCourseContent(user, course);
        return quiz;
    }

    /**
     * Retrieves a quiz by its unique identifier as an instructor, ensuring the instructor is authorized.
     *
     * @param instructor the {@link User} entity representing the instructor
     * @param quizId     the unique identifier of the quiz
     * @return the {@link Quiz} entity corresponding to the provided ID
     * @throws QuizNotFoundException           if no quiz is found with the given ID
     * @throws NotCourseInstructorException    if the user is not the instructor of the course
     */
    public Quiz findQuizByIdAsInstructor(User instructor, UUID quizId) {
        var quiz = findQuizById(instructor, quizId);
        if (!quiz.getCourseModule().getCourse().getInstructor().equals(instructor)) {
            throw new NotCourseInstructorException(instructor, quiz);
        }
        return quiz;
    }

    /**
     * Retrieves a {@link QuizDto} representation of a quiz by its unique identifier.
     *
     * @param instructor the {@link User} entity representing the instructor
     * @param quizId     the unique identifier of the quiz
     * @return a {@link QuizDto} containing quiz details
     * @throws QuizNotFoundException           if no quiz is found with the given ID
     * @throws NotCourseInstructorException    if the user is not the instructor of the course
     */
    public QuizDto getQuizDtoByQuizId(User instructor, UUID quizId) {
        var quiz = findQuizById(instructor, quizId);
        return QuizDto.from(quiz, instructor.getRole());
    }

    /**
     * Adds a new quiz to a specified module.
     * <p>
     * Creates a new {@link Quiz} entity from the provided {@link QuizDto}, associates it with the module,
     * saves the quiz and its questions and options, and updates the module's items.
     * </p>
     *
     * @param instructor the {@link User} entity representing the instructor adding the quiz
     * @param moduleId   the unique identifier of the module to which the quiz will be added
     * @param dto        the data transfer object containing quiz details
     */
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

    /**
     * Updates an existing quiz with new details.
     * <p>
     * Clears existing questions, updates the quiz based on the provided {@link QuizDto},
     * saves the updated quiz, and persists the new questions and options.
     * </p>
     *
     * @param instructor the {@link User} entity representing the instructor updating the quiz
     * @param quizId     the unique identifier of the quiz to be updated
     * @param dto        the data transfer object containing updated quiz details
     */
    @Transactional
    public void updateQuiz(User instructor, UUID quizId, @Valid QuizDto dto) {
        var quiz = findQuizByIdAsInstructor(instructor, quizId);
        quiz.getQuestions().clear();
        quiz.updateFrom(dto);
        quizRepo.save(quiz);
        saveQuestionsAndOptions(dto, quiz);
    }

    /**
     * Removes a quiz from its associated module.
     * <p>
     * Checks for existing quiz attempts before deletion. If no attempts exist, removes the quiz from the module
     * and deletes the quiz record.
     * </p>
     *
     * @param instructor the {@link User} entity representing the instructor deleting the quiz
     * @param quizId     the unique identifier of the quiz to be removed
     * @throws CantDeleteAttemptedQuizException if there are existing attempts for the quiz
     * @throws NotCourseInstructorException     if the user is not the instructor of the course
     */
    @Transactional
    public void removeQuizFromModule(User instructor, UUID quizId) {
        var quiz = findQuizByIdAsInstructor(instructor, quizId);
        var module = quiz.getCourseModule();
        var quizAttempts = quizAttemptRepo.findQuizAttemptsByQuiz(quiz, AuditEntity.SORT_BY_CREATED_AT_DESC);
        if (!quizAttempts.isEmpty()) {
            throw new CantDeleteAttemptedQuizException(instructor.getId(), quiz.getId(), quizAttempts.getFirst().getId());
        }

        // delete ModuleItem record
        var moduleItem = moduleItemService.findModuleItemByQuiz(quiz);
        module.removeModuleItem(moduleItem);

        // delete Quiz record
        quizRepo.delete(quiz);
    }

    /**
     * Saves questions and their respective options for a given quiz based on the provided {@link QuizDto}.
     *
     * @param dto  the {@link QuizDto} containing quiz details including questions and options
     * @param quiz the {@link Quiz} entity to which the questions belong
     */
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
