package QuizApp.services.question;

import QuizApp.model.question.Question;

import java.util.List;

public interface QuestionService {
    Question createQuestion(Question question);
    Question updateQuestionDetails(int questionId, Question question);
    Question getQuestionById(int questionId);
    void deleteQuestion(int questionId);
    List<Question> getRandomQuestionsForQuiz();
}
