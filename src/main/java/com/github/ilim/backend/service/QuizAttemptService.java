package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.QuizAttemptDto;
import com.github.ilim.backend.dto.QuizAttemptResultDto;
import com.github.ilim.backend.dto.UserAnswerDto;
import com.github.ilim.backend.entity.AuditEntity;
import com.github.ilim.backend.entity.Question;
import com.github.ilim.backend.entity.QuestionOption;
import com.github.ilim.backend.entity.Quiz;
import com.github.ilim.backend.entity.QuizAttempt;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.entity.UserAnswer;
import com.github.ilim.backend.entity.UserAnswerOption;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.AdminCantAttemptQuizzesException;
import com.github.ilim.backend.exception.exceptions.CantAttemptOwnQuizException;
import com.github.ilim.backend.exception.exceptions.MissingAnswerException;
import com.github.ilim.backend.exception.exceptions.QuestionOptionNotFoundException;
import com.github.ilim.backend.exception.exceptions.QuizAttemptsNotFoundException;
import com.github.ilim.backend.repo.QuestionOptionRepo;
import com.github.ilim.backend.repo.QuizAttemptRepo;
import com.github.ilim.backend.repo.UserAnswerOptionRepo;
import com.github.ilim.backend.repo.UserAnswerRepo;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing quiz attempts.
 * <p>
 * Provides functionalities such as submitting quiz attempts, retrieving quiz attempt results,
 * and allowing instructors and admins to view all attempts for a specific quiz.
 * </p>
 *
 * @see QuizAttemptRepo
 * @see UserAnswerOptionRepo
 * @see QuestionOptionRepo
 * @see QuizService
 */
@Service
@RequiredArgsConstructor
public class QuizAttemptService {

    private final UserAnswerOptionRepo userAnswerOptionRepo;
    private final QuestionOptionRepo questionOptionRepo;
    private final QuizAttemptRepo quizAttemptRepo;
    private final UserAnswerRepo userAnswerRepo;
    private final QuizService quizService;

    /**
     * Retrieves all quiz attempts made by a student for a specific quiz.
     *
     * @param student the {@link User} entity representing the student
     * @param quizId  the unique identifier of the quiz
     * @return a list of {@link QuizAttemptResultDto} containing the results of each attempt
     * @throws QuizAttemptsNotFoundException if no attempts are found for the given quiz ID
     */
    public List<QuizAttemptResultDto> getAllStudentQuizAttempts(@NonNull User student, @NonNull UUID quizId) {
        var quiz = quizService.findQuizById(student, quizId);
        var attempts = quizAttemptRepo.findQuizAttemptsByQuizAndStudent(
            quiz, student, AuditEntity.SORT_BY_CREATED_AT_DESC
        );
        if (attempts.isEmpty()) {
            throw new QuizAttemptsNotFoundException(quizId);
        }
        return attempts.stream()
            .map(QuizAttemptResultDto::from)
            .toList();
    }

    /**
     * Retrieves the most recent quiz attempt made by a student for a specific quiz.
     *
     * @param student the {@link User} entity representing the student
     * @param quizId  the unique identifier of the quiz
     * @return a {@link QuizAttemptResultDto} containing the result of the last attempt
     * @throws QuizAttemptsNotFoundException if no attempts are found for the given quiz ID
     */
    public QuizAttemptResultDto getLastStudentQuizAttempt(@NonNull User student, @NonNull UUID quizId) {
        var quiz = quizService.findQuizById(student, quizId);
        var attempts = quizAttemptRepo.findQuizAttemptsByQuizAndStudent(
            quiz, student, AuditEntity.SORT_BY_CREATED_AT_DESC
        );
        var lastAttempt = attempts.stream()
            .max(Comparator.comparing(AuditEntity::getCreatedAt))
            .orElseThrow(() -> new QuizAttemptsNotFoundException(quizId));
        return QuizAttemptResultDto.from(lastAttempt);
    }

    /**
     * Retrieves all quiz attempts for a specific quiz, accessible by instructors and admins.
     *
     * @param user    the {@link User} entity representing the instructor or admin
     * @param quizId  the unique identifier of the quiz
     * @return a list of {@link QuizAttempt} representing all attempts made by students
     */
    public List<QuizAttempt> getAllQuizAttemptsForQuiz(User user, UUID quizId) {
        var quiz = quizService.findQuizById(user, quizId);
        return quizAttemptRepo.findQuizAttemptsByQuiz(quiz, AuditEntity.SORT_BY_CREATED_AT_DESC);
    }

    /**
     * Processes a quiz attempt submitted by a student.
     * <p>
     * Evaluates the student's answers, calculates the score, determines pass/fail status,
     * and records the attempt in the repository.
     * </p>
     *
     * @param user the {@link User} entity representing the student
     * @param dto  the data transfer object containing quiz attempt details and user answers
     * @return a {@link QuizAttemptResultDto} containing the results of the attempt
     * @throws CantAttemptOwnQuizException    if a user attempts their own quiz
     * @throws AdminCantAttemptQuizzesException if an admin attempts a quiz
     * @throws MissingAnswerException         if an answer is missing for a question
     * @throws QuestionOptionNotFoundException if a selected question option does not exist
     */
    @Transactional
    public QuizAttemptResultDto processQuizAttempt(@NonNull User user, @Valid QuizAttemptDto dto) {
        var quiz = quizService.findQuizById(user, dto.getQuizId());
        if (quiz.getCourseModule().getCourse().getInstructor().getId().equals(user.getId())) {
            throw new CantAttemptOwnQuizException(user.getId(), dto.getQuizId());
        }
        if (user.getRole().equals(UserRole.ADMIN)) {
            throw new AdminCantAttemptQuizzesException(user.getId(),  quiz.getId());
        }

        // create a QuizAttempt record
        var attempt = QuizAttempt.from(user, quiz);
        quizAttemptRepo.save(attempt);  // save to get generated ID

        // Determine passed or not, then save the attempt
        var score = gradeQuiz(dto, quiz, attempt);
        var passed = score.userScore.compareTo(quiz.getPassingScore()) >= 0;
        attempt.setUserScore(score.userScore);
        attempt.setTotalScore(score.totalScore);
        attempt.setPassed(passed);
        quizAttemptRepo.save(attempt);

        return QuizAttemptResultDto.from(attempt);
    }

    /**
     * Grades a quiz attempt by evaluating the user's answers against correct options.
     *
     * @param dto     the {@link QuizAttemptDto} containing the user's answers
     * @param quiz    the {@link Quiz} entity being attempted
     * @param attempt the {@link QuizAttempt} entity representing the attempt
     * @return a {@link GradingResult} containing the user's score and the total possible score
     * @throws MissingAnswerException         if an answer is missing for a question
     * @throws QuestionOptionNotFoundException if a selected question option does not exist
     */
    private GradingResult gradeQuiz(QuizAttemptDto dto, Quiz quiz, QuizAttempt attempt) {
        var userScore = BigDecimal.ZERO;
        var quizScore = BigDecimal.ZERO;
        for (var question : quiz.getQuestions()) {
            quizScore = quizScore.add(question.getPoints());
            var answerDto = pickThisQuestionAnswer(dto, question, attempt);

            // create a UserAnswer record
            var userAnswer = UserAnswer.from(question, attempt);
            userAnswerRepo.save(userAnswer);

            // evaluate answers
            var correctOptions = questionOptionRepo.findByQuestionAndIsCorrect(
                question, true, AuditEntity.SORT_BY_CREATED_AT_DESC
            );
            if (isAnswerCorrect(correctOptions, answerDto.getSelectedOptionIds())) {
                userScore = userScore.add(question.getPoints());
            }
            saveUserAnswer(userAnswer, answerDto.getSelectedOptionIds());
        }
        return new GradingResult(userScore, quizScore);
    }

    /**
     * Saves the user's selected answer options for a given question.
     *
     * @param answer             the {@link UserAnswer} entity representing the user's answer to a question
     * @param selectedOptionIds  the list of {@link UUID} identifiers for the selected question options
     * @throws QuestionOptionNotFoundException if a selected question option does not exist
     */
    private void saveUserAnswer(UserAnswer answer, List<UUID> selectedOptionIds) {
        for (UUID optionId : selectedOptionIds) {
            var option = findQuestionOptionById(optionId);
            var answerOption = UserAnswerOption.from(answer, option);
            userAnswerOptionRepo.save(answerOption);
        }
    }

    /**
     * Finds a {@link QuestionOption} by its unique identifier.
     *
     * @param optionId the unique identifier of the question option
     * @return the {@link QuestionOption} entity corresponding to the provided ID
     * @throws QuestionOptionNotFoundException if no question option is found with the given ID
     */
    private QuestionOption findQuestionOptionById(UUID optionId) {
        return questionOptionRepo.findById(optionId)
            .orElseThrow(() -> new QuestionOptionNotFoundException(optionId));
    }

    /**
     * Retrieves the user's answer for a specific question within a quiz attempt.
     *
     * @param dto      the {@link QuizAttemptDto} containing all user answers
     * @param question the {@link Question} entity being answered
     * @param attempt  the {@link QuizAttempt} entity representing the attempt
     * @return the {@link UserAnswerDto} containing the user's selected option IDs for the question
     * @throws MissingAnswerException if no answer is found for the given question within the attempt
     */
    private static UserAnswerDto pickThisQuestionAnswer(QuizAttemptDto dto, Question question, QuizAttempt attempt) {
        return dto.getAnswers().stream()
            .filter(a -> a.getQuestionId().equals(question.getId()))
            .findFirst()
            .orElseThrow(() -> new MissingAnswerException(attempt.getId(), question.getId()));
    }

    /**
     * Determines if the user's selected answers are correct.
     * <p>
     * The user must select all correct options and no incorrect ones to be considered correct.
     * </p>
     *
     * @param correctOptions     the collection of {@link QuestionOption} entities that are correct for the question
     * @param selectedOptionIds  the list of {@link UUID} identifiers for the options selected by the user
     * @return {@code true} if the selected options exactly match the correct options, {@code false} otherwise
     */
    private boolean isAnswerCorrect(Collection<QuestionOption> correctOptions, List<UUID> selectedOptionIds) {
        // student must select all correct options, no less no more
        var correctOptionIds = correctOptions.stream()
            .map(QuestionOption::getId)
            .collect(Collectors.toSet());
        var selectedIds = new HashSet<>(selectedOptionIds);
        return correctOptionIds.equals(selectedIds);
    }

    /**
     * Represents the result of grading a quiz attempt.
     *
     * @param userScore  the total score obtained by the user
     * @param totalScore the total possible score for the quiz
     */
    private record GradingResult(BigDecimal userScore, BigDecimal totalScore) {}
}
