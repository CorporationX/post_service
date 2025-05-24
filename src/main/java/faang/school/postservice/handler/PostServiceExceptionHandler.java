package faang.school.postservice.handler;

import faang.school.postservice.dto.error.PostServiceErrorResponseDto;
import faang.school.postservice.exception.authorization.UserUnauthorizedException;
import faang.school.postservice.exception.post.PostAlreadyPublishedException;
import faang.school.postservice.exception.post.PostNotFoundException;
import faang.school.postservice.exception.project_service_client.ProjectNotFoundException;
import faang.school.postservice.exception.user_service_client.UserNotFoundException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class PostServiceExceptionHandler {
    private static final Map<Class<? extends Exception>, HttpStatus> httpStatusMap = Map.of(
            UserUnauthorizedException.class, HttpStatus.UNAUTHORIZED,
            PostNotFoundException.class, HttpStatus.NOT_FOUND,
            PostAlreadyPublishedException.class, HttpStatus.CONFLICT,
            UserNotFoundException.class, HttpStatus.NOT_FOUND,
            ProjectNotFoundException.class, HttpStatus.NOT_FOUND,
            MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST,
            FeignException.class, HttpStatus.BAD_GATEWAY
    );
    private static final Map<Class<? extends Exception>, ErrorHandler> errorHandlers = Map.of(
            MethodArgumentNotValidException.class, ex ->
                    formatMethodArgumentNotValidException((MethodArgumentNotValidException) ex)
    );

    @ExceptionHandler({
            UserUnauthorizedException.class,
            PostNotFoundException.class,
            UserNotFoundException.class,
            PostNotFoundException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<PostServiceErrorResponseDto> handleException(Exception ex) {
        ErrorHandler handler = getErrorHandler(ex);
        String errorMessage = handler.handle(ex);
        HttpStatus status = getHttpStatus(ex);

        return createErrorResponse(errorMessage, status, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<PostServiceErrorResponseDto> handleGenericException(Exception ex) {
        log.error("Unhandled exception caught", ex);
        return createErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    private HttpStatus getHttpStatus(Throwable ex) {
        return httpStatusMap.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorHandler getErrorHandler(Throwable ex) {
        return errorHandlers.getOrDefault(ex.getClass(), Throwable::getMessage);
    }

    private ResponseEntity<PostServiceErrorResponseDto> createErrorResponse(String errorMsg,
                                                                            HttpStatus status,
                                                                            Exception ex) {
        log.error("Error in GoalController: {}, response status {}", errorMsg, status, ex);
        PostServiceErrorResponseDto response =
                new PostServiceErrorResponseDto(errorMsg, LocalDateTime.now(), status.value());
        return new ResponseEntity<>(response, status);
    }

    private static String formatMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getAllErrors().stream()
                .map(error -> String.format("Field '%s' %s",
                        ((FieldError) error).getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));
    }
}