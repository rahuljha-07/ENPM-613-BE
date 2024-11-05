package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.QuizAttemptDto;
import com.github.ilim.backend.dto.QuizAttemptResultDto;
import com.github.ilim.backend.dto.UserAnswerDto;
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
import com.github.ilim.backend.repo.QuestionOptionRepo;
import com.github.ilim.backend.repo.QuestionRepo;
import com.github.ilim.backend.repo.QuizAttemptRepo;
import com.github.ilim.backend.repo.UserAnswerOptionRepo;
import com.github.ilim.backend.repo.UserAnswerRepo;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.engine.spi.CollectionEntry;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizAttemptService {

    private final QuizService quizService;
    private final QuizAttemptRepo quizAttemptRepo;
    private final QuestionRepo questionRepo;
    private final QuestionOptionRepo questionOptionRepo;
    private final UserAnswerRepo userAnswerRepo;
    private final UserAnswerOptionRepo userAnswerOptionRepo;
    private final CourseService courseService;

    @Transactional
    public QuizAttemptResultDto processQuizAttempt(User user, @Valid QuizAttemptDto dto) {
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
            var correctOptions = questionOptionRepo.findByQuestionAndIsCorrect(question, true);
            if (isAnswerCorrect(correctOptions, answerDto.getSelectedOptionIds())) {
                userScore = userScore.add(question.getPoints());
            }
            saveUserAnswer(userAnswer, answerDto.getSelectedOptionIds());
        }
        return new GradingResult(userScore, quizScore);
    }

    private void saveUserAnswer(UserAnswer answer, List<UUID> selectedOptionIds) {
        for (UUID optionId : selectedOptionIds) {
            var option = findQuestionOptionById(optionId);
            var answerOption = UserAnswerOption.from(answer, option);
            userAnswerOptionRepo.save(answerOption);
        }
    }

    private QuestionOption findQuestionOptionById(UUID optionId) {
        return questionOptionRepo.findById(optionId)
            .orElseThrow(() -> new QuestionOptionNotFoundException(optionId));
    }

    private static UserAnswerDto pickThisQuestionAnswer(QuizAttemptDto dto, Question question, QuizAttempt attempt) {
        return dto.getAnswers().stream()
            .filter(a -> a.getQuestionId().equals(question.getId()))
            .findFirst()
            .orElseThrow(() -> new MissingAnswerException(attempt.getId(), question.getId()));
    }

    private boolean isAnswerCorrect(Collection<QuestionOption> correctOptions, List<UUID> selectedOptionIds) {
        // student must select all correct options, no less no more
        var correctOptionIds = correctOptions.stream()
            .map(QuestionOption::getId)
            .collect(Collectors.toSet());
        var selectedIds = new HashSet<>(selectedOptionIds);
        return correctOptionIds.equals(selectedIds);
    }

    private record GradingResult(BigDecimal userScore, BigDecimal totalScore) {}
}
