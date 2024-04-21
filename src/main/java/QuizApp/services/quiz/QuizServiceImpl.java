package QuizApp.services.quiz;


import QuizApp.exceptions.AccessDeniedException;
import QuizApp.exceptions.ObjectNotFoundException;
import QuizApp.model.question.Question;
import QuizApp.model.quiz.Quiz;
import QuizApp.model.user.User;
import QuizApp.repositories.QuizRepository;
import QuizApp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import QuizApp.services.question.QuestionService;
import QuizApp.services.user.UserService;

import java.util.List;


@Service
@Transactional
public class QuizServiceImpl implements QuizService{
    private final UserService userService;
    private final QuestionService questionService;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    @Autowired
    public QuizServiceImpl(UserService userService, QuestionService questionService, QuizRepository quizRepository, UserRepository userRepository) {

        this.userService = userService;
        this.questionService = questionService;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
    }

    public boolean isUserQuizOwner(int quizId, int userId) {
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        return quiz != null && quiz.getUser().getUserId() == userId;
    }


    @Override
    public Quiz createQuiz(int userId) {
        User user = userService.getUser(userId);
        List<Question> questions = questionService.getRandomQuestionsForQuiz();
        Quiz quiz = new Quiz();
        quiz.setUser(user);
        quiz.setQuestions(questions);

        return quizRepository.save(quiz);
    }

    @Override
    public Quiz getQuiz(int quizId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();
        int currentUserId = user.getUserId();

        if (!isUserQuizOwner(quizId, currentUserId)) {
            throw new AccessDeniedException("Access is denied: User does not own this quiz.");
        }

        return quizRepository.findById(quizId)
                .orElseThrow(() -> new ObjectNotFoundException("Quiz not found with ID: " + quizId));
    }

    @Override
    public List<Quiz> listQuizzesForUser(int userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails currentUser = (UserDetails) authentication.getPrincipal();
        User authenticatedUser = (User) currentUser;

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (authenticatedUser.getUserId() != userId && !isAdmin) {
            throw new AccessDeniedException("Access denied: You are not authorized to view these quizzes.");
        }
        return quizRepository.findByUserUserId(userId);
    }

    @Override
    public Quiz submitAnswers(int quizId, List<Integer> answerIds) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();
        int currentUserId = user.getUserId();

        if (!isUserQuizOwner(quizId, currentUserId)) {
            throw new AccessDeniedException("Access is denied: User does not own this quiz.");
        }

        Quiz quiz = getQuiz(quizId);
//
//        long score = quiz.getQuestions().stream()
//                .flatMap(question -> question.getOptions().stream())
//                .filter(option -> answerIds.contains(option.getOptionId()) && option.isIfCorrect())
//                .count();
//
//        quiz.setScore(score/4);
        return quizRepository.save(quiz);

    }

    @Override
    public void deleteQuiz(int quizId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUserName(userDetails.getUsername());

        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));
        boolean isOwner = quiz.getUser().getUserId() == user.getUserId();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Access denied: You are not authorized to delete this quiz.");
        }
        quizRepository.deleteById(quizId);
    }
}
