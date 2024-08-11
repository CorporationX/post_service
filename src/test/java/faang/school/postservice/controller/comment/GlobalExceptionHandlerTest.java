package faang.school.postservice.controller.comment;

import faang.school.postservice.exception.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;
    @Mock
    private BindingResult bindingResult;

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
    void whenIllegalArgumentExceptionThenReturns400() {
        // given - precondition
        var expectedErrors = ErrorResponse.builder()
                .message("Not found")
                .status(BAD_REQUEST.value())
                .error("Illegal Argument")
                .build();

        // when - action
        var actualErrorResponse = globalExceptionHandler.handleIllegalArgumentException(illegalArgumentException);

        // then - verify the output
        assertThat(actualErrorResponse.getMessage()).isEqualTo(expectedErrors.getMessage());
        assertThat(actualErrorResponse.getStatus()).isEqualTo(expectedErrors.getStatus());
        assertThat(actualErrorResponse.getError()).isEqualTo(expectedErrors.getError());
    }
}