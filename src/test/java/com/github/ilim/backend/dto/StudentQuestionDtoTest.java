package com.github.ilim.backend.dto;

import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.CourseModule;
import com.github.ilim.backend.entity.Question;
import com.github.ilim.backend.entity.QuestionOption;
import com.github.ilim.backend.entity.Quiz;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.CourseStatus;
import com.github.ilim.backend.enums.QuestionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StudentQuestionDtoTest {

    @Test
    void testFrom_Success() {
        UUID questionId = UUID.randomUUID();
        UUID quizId = UUID.randomUUID();
        UUID optionId1 = UUID.randomUUID();
        UUID optionId2 = UUID.randomUUID();

        // Create a course
        var course = new Course();
        course.setTitle("Test Course");
        course.setPrice(new BigDecimal("49.99"));
        course.setStatus(CourseStatus.DRAFT);
        course.setInstructor(new User());

        var module = new CourseModule();
        module.setId(UUID.randomUUID());
        module.setCourse(course);
        module.setTitle("title");
        module.setOrderIndex(0);
        module.setDescription("description");

        // Create quiz
        var quiz = new Quiz();
        quiz.setId(quizId);
        quiz.setTitle("Test Quiz");
        quiz.setPassingScore(new BigDecimal("70"));
        quiz.setCourseModule(module);

        Question question = new Question();
        question.setId(questionId);
        question.setText("What is 2 + 2?");
        question.setType(QuestionType.MULTIPLE_CHOICE);
        question.setPoints(new BigDecimal("10"));
        question.setOrderIndex(1);
        question.setQuiz(quiz);

        QuestionOption option1 = new QuestionOption();
        option1.setId(optionId1);
        option1.setText("4");
        option1.setIsCorrect(true);
        option1.setQuestion(question);

        QuestionOption option2 = new QuestionOption();
        option2.setId(optionId2);
        option2.setText("5");
        option2.setIsCorrect(false);
        option2.setQuestion(question);

        question.setOptions(List.of(option1, option2));

        StudentQuestionDto dto = StudentQuestionDto.from(question);

        assertNotNull(dto);
        assertEquals(questionId, dto.id());
        assertEquals("What is 2 + 2?", dto.text());
        assertEquals(QuestionType.MULTIPLE_CHOICE, dto.type());
        assertEquals(new BigDecimal("10"), dto.points());
        assertEquals(1, dto.orderIndex());
        assertEquals(quizId, dto.quizId());
        assertEquals(2, dto.options().size());
        assertEquals(optionId1, dto.options().get(0).id());
        assertEquals("4", dto.options().get(0).text());
        assertEquals(optionId2, dto.options().get(1).id());
        assertEquals("5", dto.options().get(1).text());
    }
}
