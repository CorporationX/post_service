package faang.school.postservice.exception.handler;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.FeignClientException;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeignExceptionHandlerTest {

    private final FeignExceptionHandler feignExceptionHandler = new FeignExceptionHandler();

    private final String entityName = "user";
    private final Long entityId = 42L;

    @Test
    void handleFeignExceptionThrowsPassedExceptionForFeign404() {
        FeignException notFound = new FeignException.NotFound(
                "User not found",
                Request.create(Request.HttpMethod.GET, "/users/42", Map.of(), null, null, null),
                null,
                null
        );

        RuntimeException thrownException = assertThrows(
                EntityNotFoundException.class,
                () -> feignExceptionHandler.handleFeignException(
                        notFound,
                        entityName,
                        entityId
                )
        );

        assertInstanceOf(EntityNotFoundException.class, thrownException);
        assertTrue(thrownException.getMessage().contains(entityName));
        assertTrue(thrownException.getMessage().contains(entityId.toString()));
    }

    @Test
    void handleFeignExceptionThrowsFeignClientExceptionForOtherErrors() {
        FeignException otherException = new FeignException.InternalServerError(
                "Internal error",
                Request.create(Request.HttpMethod.GET, "/users/42", Map.of(), null, null, null),
                null,
                null
        );

        RuntimeException thrownException = assertThrows(
                FeignClientException.class,
                () -> feignExceptionHandler.handleFeignException(
                        otherException,
                        entityName,
                        entityId
                )
        );

        assertInstanceOf(FeignClientException.class, thrownException);
        assertTrue(thrownException.getMessage().contains(entityName));
        assertTrue(thrownException.getMessage().contains(entityId.toString()));
    }


}