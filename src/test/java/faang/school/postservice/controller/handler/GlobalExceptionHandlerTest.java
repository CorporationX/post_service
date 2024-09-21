package faang.school.postservice.controller.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private WebRequest request;

    private MethodArgumentNotValidException methodArgumentNotValidException;
    private IllegalArgumentException illegalArgumentException;

    @BeforeEach
    public void setUp() throws NoSuchMethodException {
        FieldError fieldErrorContent = new FieldError("commentDto", "content", "Content should not be blank");
        lenient()
                .when(bindingResult.getAllErrors())
                .thenReturn(of(fieldErrorContent));

        MethodParameter methodParameter = new MethodParameter(String.class.getConstructors()[0], -1);

        methodArgumentNotValidException = new MethodArgumentNotValidException(methodParameter, bindingResult);

        illegalArgumentException = new IllegalArgumentException("Not found");
    }

    @Test
    void whenMethodArgumentNotValidExceptionThenReturns400() throws Exception {
        // given - precondition
        Map<String, String> expectedErrors = new HashMap<>();
        expectedErrors.put("content", "Content should not be blank");

        // when - action
        var actualErrorResponse = globalExceptionHandler.handleValidationException(methodArgumentNotValidException);

        // then - verify the output
        assertThat(actualErrorResponse.getMessage()).isEqualTo(expectedErrors.toString());
        assertThat(actualErrorResponse.getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(actualErrorResponse.getError()).isEqualTo("Validation Failed");
    }

    @Test
    @SuppressWarnings("unchecked")
    void whenIllegalArgumentExceptionThenReturns400() {
        // given - precondition
        Map<String, Object> expectedBody = new HashMap<>();
        expectedBody.put("timestamp", LocalDateTime.now());
        expectedBody.put("status", HttpStatus.BAD_REQUEST.value());
        expectedBody.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        expectedBody.put("message",  illegalArgumentException.getMessage());
        expectedBody.put("path", "/example-path");

        when(request.getDescription(false))
                .thenReturn("uri=/example-path");

        // when - action
        var actualErrorResponse = globalExceptionHandler.handleIllegalArgumentException(illegalArgumentException, request);
        Map<String, Object> actualBody = (Map<String, Object>) actualErrorResponse.getBody();

        // then - verify the output
        assertThat(actualErrorResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(actualBody).isNotNull();
        assertThat(actualBody.get("status")).isEqualTo(expectedBody.get("status"));
        assertThat(actualBody.get("error")).isEqualTo(expectedBody.get("error"));
        assertThat(actualBody.get("message")).isEqualTo(expectedBody.get("message"));
        assertThat(actualBody.get("path")).isEqualTo(expectedBody.get("path"));
        assertThat((LocalDateTime) actualBody.get("timestamp"))
                .isCloseTo((LocalDateTime) expectedBody.get("timestamp"), within(1, ChronoUnit.SECONDS));
    }
}