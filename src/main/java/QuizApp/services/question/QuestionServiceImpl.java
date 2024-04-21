package QuizApp.services.question;


import QuizApp.model.question.Question;
import QuizApp.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


@Service
@Transactional
public class QuestionServiceImpl implements QuestionService{

    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public Question updateQuestionDetails(int questionId, Question updatedQuestion) {
        Question existingQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new NoSuchElementException("Question not found with ID: " + questionId));

        if (updatedQuestion.getQuesDetails() != null && !updatedQuestion.getQuesDetails().trim().isEmpty()) {
            existingQuestion.setQuesDetails(updatedQuestion.getQuesDetails());
        }
        if (updatedQuestion.getOptions() != null && !updatedQuestion.getOptions().isEmpty()) {
            if (!shouldUpdateOptions(updatedQuestion.getOptions())) {
                throw new IllegalArgumentException("Four of the options must be uniquely labeled with 'A', 'B', 'C', or 'D'");
            }
            existingQuestion.setOptions(updatedQuestion.getOptions());
        }
        if (updatedQuestion.getAnswer() != null && !updatedQuestion.getAnswer().isEmpty()) {
            existingQuestion.setAnswer(updatedQuestion.getAnswer());
        }

        return questionRepository.save(existingQuestion);
    }

    private boolean shouldUpdateOptions(Map<String, String> updatedOptions) {
        return updatedOptions.size() == 4 &&
                updatedOptions.containsKey("A") &&
                updatedOptions.containsKey("B") &&
                updatedOptions.containsKey("C") &&
                updatedOptions.containsKey("D");
    }


    @Override
    public Question getQuestionById(int questionId) {
       return questionRepository.findById(questionId)
                .orElseThrow(() -> new NoSuchElementException("Question not found with ID: " + questionId));
    }

    @Override
    public void deleteQuestion(int questionId) {
        Question question = getQuestionById(questionId);
        questionRepository.delete(question);
    }

    @Override
    public List<Question> getRandomQuestionsForQuiz() {
        return questionRepository.findRandomQuestions();
    }
}
