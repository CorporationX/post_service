package faang.school.postservice.validator;

import faang.school.postservice.dto.text.gears.TextGearsResponse;
import faang.school.postservice.exception.TextGearsException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TextGearsValidatorImplTest {

    private final TextGearsValidatorImpl textGearsValidator = new TextGearsValidatorImpl();

    @Test
    public void testIsCorrectResponse_NullResponse() {
        TextGearsException exception = assertThrows(TextGearsException.class,
                () -> textGearsValidator.isCorrectResponse(null));
        assertEquals("Response is null", exception.getMessage());
    }

    @Test
    public void testIsCorrectResponse_NullInnerResponse() {
        TextGearsResponse response = new TextGearsResponse();

        TextGearsException exception = assertThrows(TextGearsException.class,
                () -> textGearsValidator.isCorrectResponse(response));

        assertEquals("Response is null", exception.getMessage());
    }

    @Test
    public void testIsCorrectResponse_ErrorStatus() {
        String description = "Error description";
        TextGearsResponse response = TextGearsResponse.builder()
                .status(false)
                .response(new TextGearsResponse.Response())
                .description(description)
                .build();

        TextGearsException exception = assertThrows(TextGearsException.class,
                () -> textGearsValidator.isCorrectResponse(response));

        assertEquals(description, exception.getMessage());
    }

    @Test
    public void testIsCorrectResponse_Success() {
        String correctText = "Correct text";
        TextGearsResponse response = TextGearsResponse.builder()
                .status(true)
                .response(new TextGearsResponse.Response(correctText))
                .build();

        assertDoesNotThrow(() -> textGearsValidator.isCorrectResponse(response));
    }
}