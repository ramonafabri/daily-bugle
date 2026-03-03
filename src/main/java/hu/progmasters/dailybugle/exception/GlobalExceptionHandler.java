package hu.progmasters.dailybugle.exception;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Locale;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @Autowired
    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationError>> handleValidationException(MethodArgumentNotValidException exception) {
        List<ValidationError> validationErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ValidationError(fieldError.getField(), messageSource.getMessage(fieldError, Locale.getDefault())))
                .toList();
        return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ArticleNotFoundException.class)
    public ResponseEntity<ApiError> handleArticleNotFoundException(ArticleNotFoundException exception) {
        ApiError body = new ApiError("ARTICLE_NOT_FOUND", "Article not found", exception.getMessage());
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyExistsException(EmailAlreadyExistsException exception) {
        ApiError body = new ApiError("EMAIL_ALREADY_EXISTS", "Email already exists", exception.getMessage());
        HttpStatus status = HttpStatus.CONFLICT;
        return new ResponseEntity<>(body, status);
    }


    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentialsException(InvalidCredentialsException exception) {
        ApiError body = new ApiError("INVALID_EMAIL_OR_PASSWORD", "Invalid email or password", exception.getMessage());
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException exception) {
        ApiError body = new ApiError("ACCESS_DENIED", "Access denied", exception.getMessage());
        HttpStatus status = HttpStatus.FORBIDDEN;
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(AlreadyRatedException.class)
    public ResponseEntity<ApiError> handleAlreadyRatedException(AlreadyRatedException exception) {
        ApiError body = new ApiError("ALREADY_RATED", "User already rated this article", exception.getMessage());
        HttpStatus status = HttpStatus.CONFLICT;
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException exception) {
        ApiError body = new ApiError("USER_NOT_FOUND", "User nor found", exception.getMessage());
        HttpStatus status = HttpStatus.CONFLICT;
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(InvalidAdminOperationException.class)
    public ResponseEntity<ApiError> handleInvalidAdminOperationException(InvalidAdminOperationException exception) {
        ApiError body = new ApiError("INVALID_ADMIN_OPERATION", "Invalid admin operation", exception.getMessage());
        HttpStatus status = HttpStatus.CONFLICT;
        return new ResponseEntity<>(body, status);
    }


}
