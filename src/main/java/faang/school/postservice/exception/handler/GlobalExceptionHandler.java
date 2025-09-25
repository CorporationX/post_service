package faang.school.postservice.exception.handler;

import faang.school.postservice.dto.error.ErrorResponse;
import faang.school.postservice.exception.EntityAlreadyLikedException;
import faang.school.postservice.exception.EntityDeletedException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ExternalServiceException;
import faang.school.postservice.exception.FailedFeedHeatException;
import faang.school.postservice.exception.HeaderNotFoundException;
import faang.school.postservice.exception.MoreOneOwnerException;
import faang.school.postservice.exception.NotResourceOwnerException;
import faang.school.postservice.exception.OwnerIdNotPresentException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MoreOneOwnerException.class)
    public ErrorResponse handleMoreOneOwner(MoreOneOwnerException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse(e.getErrorType().getErrorCode(), e.getErrorType().getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(OwnerIdNotPresentException.class)
    public ErrorResponse handleOwnerIdNotPresent(OwnerIdNotPresentException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse(e.getErrorType().getErrorCode(), e.getErrorType().getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EntityDeletedException.class)
    public ErrorResponse handleEntityDeleted(EntityDeletedException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Entity deleted", e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EntityAlreadyLikedException.class)
    public ErrorResponse handleEntityAlreadyLiked(EntityAlreadyLikedException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Entity already liked", e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(FeignException.class)
    public ErrorResponse handleFeignException(FeignException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("User not found", e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleEntityNotFound(EntityNotFoundException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Entity not found", e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(HeaderNotFoundException.class)
    public ErrorResponse handleHeaderNotFound(HeaderNotFoundException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Header not found", e.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(NotResourceOwnerException.class)
    public ErrorResponse handleNotResourceOwner(NotResourceOwnerException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Not resource owner", e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ExternalServiceException.class)
    public ErrorResponse handleExternalService(ExternalServiceException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Feed heat error", e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(FailedFeedHeatException.class)
    public ErrorResponse handleFailedFeedHeat(FailedFeedHeatException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Feed heat error", e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handleRuntimeException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Unknown error", e.getMessage());
    }
}
