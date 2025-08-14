package faang.school.postservice.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.exception.ErrorResponse;
import faang.school.postservice.exception.UserNotFoundException;
import feign.FeignException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeignClientValidator {

    private final ObjectMapper objectMapper;

    public void validateById(@NotNull Runnable checker, String errorMessage) {
        /* todo: заменить "Runnable checker" на "Supplier checker", метод Supplier.get должен возвращать dto
         *  объекта, который проверяем
         */
        try {
            checker.run();
        } catch (FeignException fe) {
            log.error("FeignException.status is: [{}]", fe.status());
            log.error("object: {}", fe.getMessage(), fe);
            StringBuilder resultMessage = new StringBuilder();
            if (fe.status() == -1) {
                resultMessage.append(errorMessage);
            } else {
                String feignExceptionMessage = fe.contentUTF8();
                if (feignExceptionMessage != null) {
                    try {
                        ErrorResponse errorResponse = objectMapper.readValue(
                            feignExceptionMessage, ErrorResponse.class);
                        resultMessage.append(errorResponse.getErrorMessage());
                    } catch (JsonProcessingException e) {
                        log.error("JsonProcessingException: {}", e.getMessage(), e);
                        resultMessage.append(errorMessage);
                    }
                } else {
                    resultMessage.append(errorMessage);
                }
            }
            throw new UserNotFoundException(resultMessage.toString());
        }
    }
}
