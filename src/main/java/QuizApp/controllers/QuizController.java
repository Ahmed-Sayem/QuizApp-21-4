package QuizApp.controllers;

import javax.validation.Valid;

import QuizApp.exceptions.InvalidAnswerException;
import QuizApp.exceptions.QuizNotFoundException;
import QuizApp.exceptions.UserNotFoundException;
import QuizApp.model.quiz.AnswerInput;
import QuizApp.model.quiz.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import QuizApp.services.quiz.QuizService;

import java.util.List;

@RestController
@RequestMapping("/quizzes")
public class QuizController {
    private final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/create/{userId}")
    public ResponseEntity<Quiz> createQuiz(@PathVariable int userId) {
        try {
            Quiz quiz = quizService.createQuiz(userId);
            return new ResponseEntity<>(quiz, HttpStatus.CREATED);
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  // Consider custom error handling or messages.
        }
    }


    @GetMapping("/{quizId}")
    public ResponseEntity<Quiz> getQuiz(@PathVariable int quizId) {
        Quiz quiz = quizService.getQuiz(quizId);
        if (quiz != null) {
            return ResponseEntity.ok(quiz);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Quiz>> listQuizzesForUser(@PathVariable int userId) {
        try {
            List<Quiz> quizzes = quizService.listQuizzesForUser(userId);
            return ResponseEntity.ok(quizzes);
        } catch (UserNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/submit/{quizId}")
    public ResponseEntity<?> submitAnswers(@PathVariable int quizId, @Valid @RequestBody AnswerInput answers) {
        try {
            // Assuming submitAnswers returns a result object or similar.
            QuizResult result = quizService.submitAnswers(quizId, answers.getAnswerIds());
            return ResponseEntity.ok(result);
        } catch (InvalidAnswerException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (QuizNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable int quizId) {
        quizService.deleteQuiz(quizId);
        return ResponseEntity.noContent().build();
    }
}
