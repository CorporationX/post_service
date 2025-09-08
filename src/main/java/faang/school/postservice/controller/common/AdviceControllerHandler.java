package faang.school.postservice.controller.common;

import faang.school.postservice.controller.PostController;
import faang.school.postservice.exception.NotSupportedDataException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static faang.school.postservice.controller.common.ApiExceptionDto.ErrorType.BUSINESS_ERROR;
import static faang.school.postservice.controller.common.ApiExceptionDto.ErrorType.SERVER_ERROR;


@RestControllerAdvice(assignableTypes = {PostController.class})
@Slf4j
public class AdviceControllerHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ExceptionHandler(EntityNotFoundException.class)
    public ApiExceptionDto entityNotFoundException(final EntityNotFoundException e) {
        log.error(e.getMessage());
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto();
        apiExceptionDto.setMessage(e.getMessage());
        apiExceptionDto.setStatus(HttpStatus.NOT_FOUND.name());
        apiExceptionDto.setTimestamp(System.currentTimeMillis());
        apiExceptionDto.setErrorType(SERVER_ERROR);
        return apiExceptionDto;
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    @ExceptionHandler(NotSupportedDataException.class)
    public ApiExceptionDto NotSupportedDataException(final NotSupportedDataException e) {
        log.error(e.getMessage());
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto();
        apiExceptionDto.setMessage(e.getMessage());
        apiExceptionDto.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.name());
        apiExceptionDto.setTimestamp(System.currentTimeMillis());
        apiExceptionDto.setErrorType(BUSINESS_ERROR);
        return apiExceptionDto;
    }
}
