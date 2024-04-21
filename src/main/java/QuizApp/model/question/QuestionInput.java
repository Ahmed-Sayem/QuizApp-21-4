package QuizApp.model.question;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import QuizApp.annotation.ValidOptions;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;


import java.util.Map;

@Getter
@Setter
public class QuestionInput {
    @NotBlank(message = "Question details must be provided")
    @Size(min = 8,max = 60, message = "This is too short for a question!")
    private String quesDetails;

    @NotNull(message = "Options must be provided")
    @ValidOptions(message = "Each option must be uniquely labeled with 'A', 'B', 'C', or 'D' and QuestionDetails must not be empty, having 1 to 30 characters.")
    private Map<String, String> options;

    @NotBlank(message = "Answer must be provided")
    @Pattern(regexp = "[ABCD]", message = "Answer must be one of 'A', 'B', 'C', 'D'")
    private String answer;
}

