package faang.school.postservice.controller.post;

import faang.school.postservice.exception.ErrorResponse;
import faang.school.postservice.exception.redis.RedisTransactionInterrupted;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

class RedisExceptionHandlerTest {
    private static final String TEST_MESSAGE = "test message";

    private final RedisExceptionHandler redisExceptionHandler = new RedisExceptionHandler();

    @Test
    void testHandleRedisTransactionInterrupted() {
        ResponseEntity<ErrorResponse> response = redisExceptionHandler
                .handleRedisTransactionInterrupted(new RedisTransactionInterrupted(TEST_MESSAGE));

        assertThat(response)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(INTERNAL_SERVER_ERROR);
        assertThat(response.getBody())
                .extracting(ErrorResponse::getMessage)
                .isEqualTo(TEST_MESSAGE);
    }
}