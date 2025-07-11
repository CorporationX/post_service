package faang.school.postservice.validation;

import faang.school.postservice.dto.post.PostRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OneOwnerOnlyValidatorTest {

    private OneOwnerOnlyValidator validator;

    @BeforeEach
    void setUp() {
        validator = new OneOwnerOnlyValidator();
    }

    @Test
    void valid_when_only_authorId() {
        PostRequestDto dto = new PostRequestDto("Test", 1L, null);
        assertTrue(validator.isValid(dto, null));
    }

    @Test
    void valid_when_only_projectId() {
        PostRequestDto dto = new PostRequestDto("Test", null, 1L);
        assertTrue(validator.isValid(dto, null));
    }

    @Test
    void invalid_when_both_null() {
        PostRequestDto dto = new PostRequestDto("Test", null, null);
        assertFalse(validator.isValid(dto, null));
    }

    @Test
    void invalid_when_both_present() {
        PostRequestDto dto = new PostRequestDto("Test", 1L, 2L);
        assertFalse(validator.isValid(dto, null));
    }
}
