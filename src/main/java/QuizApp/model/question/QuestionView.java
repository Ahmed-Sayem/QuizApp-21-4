package QuizApp.model.question;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Map;

@JsonPropertyOrder({"quesId","quesDetails", "options","answer"})
public interface QuestionView {
    int getQuesId();
    String getQuesDetails();
    Map<String, String> getOptions();
    String getAnswer();
}
