package QuizApp.controllers;

import javax.validation.Valid;
import QuizApp.model.question.Question;
import QuizApp.model.question.QuestionInput;
import QuizApp.model.question.QuestionUpdate;
import QuizApp.model.question.QuestionView;
import QuizApp.repositories.QuestionRepository;
import QuizApp.services.quiz.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import QuizApp.quizObjectMapper.QuizObjectMapper;
import QuizApp.services.question.QuestionService;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionService questionService;
    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionController(QuestionService questionService, QuestionRepository questionRepository) {
        this.questionService = questionService;
        this.questionRepository = questionRepository;
    }

    @PostMapping
    public ResponseEntity<QuestionView> createQuestion(@Valid @RequestBody QuestionInput questionInput) {
        Question question = QuizObjectMapper.convertQuestionInputToModel(questionInput);
        Question createdQuestion = questionService.createQuestion(question);
        QuestionView createdQuestionView = questionRepository.findQuestionViewByQuesId(createdQuestion.getQuesId());
        return ResponseEntity.ok(createdQuestionView);
    }


    @PutMapping("/{questionId}")
    public ResponseEntity<QuestionView> updateQuestionDetails(@PathVariable int questionId, @Valid @RequestBody QuestionUpdate questionUpdate) {
        try {
            Question question = QuizObjectMapper.convertQuestionUpdateToModel(questionUpdate);
            questionService.updateQuestionDetails(questionId, question);
            QuestionView updatedQuestionView = questionRepository.findQuestionViewByQuesId(questionId);
            return ResponseEntity.ok(updatedQuestionView);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/{questionId}")
    public ResponseEntity<?> getQuestion(@PathVariable int questionId) {
        QuestionView questionView = questionRepository.findQuestionViewByQuesId(questionId);
        if (questionView != null) {
            return ResponseEntity.ok(questionView);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Question not found with ID: " + questionId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }


    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable int questionId) {
        try {
            questionService.deleteQuestion(questionId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
