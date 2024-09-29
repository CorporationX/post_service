package faang.school.postservice.dto.comment;

import faang.school.postservice.dto.comment.validation.group.Create;
import faang.school.postservice.dto.comment.validation.group.Update;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommentDtoTest {
    private static final int BIGGER_THAN_MAX_LENGTH = 5000;
    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    @DisplayName("Create CommentDto for creating comment")
    void commentDtoTest_ValidateCreatingCommentDtoForCreatingComment() {
        CommentDto commentDto = initCommentDto(null, "test", 1L, null);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto, Create.class);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Create CommentDto for creating comment with id")
    void commentDtoTest_ValidateCreatingCommentDtoForCreatingCommentWithId() {
        CommentDto commentDto = initCommentDto(1L, "test", 1L, null);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto, Create.class);

        assertEquals(1, violations.size());
        assertEquals("Id not required", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Create CommentDto for creating comment with empty content")
    void commentDtoTest_ValidateCreatingCommentDtoForCreatingCommentWithEmptyContent() {
        CommentDto commentDto = initCommentDto(null, "", 1L, null);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto, Create.class);

        assertEquals(1, violations.size());
        assertEquals("Content for comment is required, and cannot be empty or blank",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Create CommentDto for creating comment with blank content")
    void commentDtoTest_ValidateCreatingCommentDtoForCreatingCommentWithBlankContent() {
        CommentDto commentDto = initCommentDto(null, "   ", 1L, null);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto, Create.class);

        assertEquals(1, violations.size());
        assertEquals("Content for comment is required, and cannot be empty or blank",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Create CommentDto for creating comment with null content")
    void commentDtoTest_ValidateCreatingCommentDtoForCreatingCommentWithNullContent() {
        CommentDto commentDto = initCommentDto(null, null, 1L, null);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto, Create.class);

        assertEquals(1, violations.size());
        assertEquals("Content for comment is required, and cannot be empty or blank",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Create CommentDto for creating comment with length of content bigger than max length")
    void commentDtoTest_ValidateCreatingCommentDtoForCreatingCommentWithLengthOfContentBiggerThanMaxLength() {
        String content = "a".repeat(BIGGER_THAN_MAX_LENGTH);
        CommentDto commentDto = initCommentDto(null, content, 1L, null);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto, Create.class);

        assertEquals(1, violations.size());
        assertEquals("Content length must not be more than 4096 symbols",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Create CommentDto for creating comment with null author id")
    void commentDtoTest_ValidateCreatingCommentDtoForCreatingCommentWithNullAuthorId() {
        CommentDto commentDto = initCommentDto(null, "test", null, null);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto, Create.class);

        assertEquals(1, violations.size());
        assertEquals("Author id is required",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Create CommentDto for creating comment with author id not positive")
    void commentDtoTest_ValidateCreatingCommentDtoForCreatingCommentWithAuthorIdNotPositive() {
        CommentDto commentDto = initCommentDto(null, "test", 0L, null);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto, Create.class);

        assertEquals(1, violations.size());
        assertEquals("Author id must be positive",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Create CommentDto for creating comment with updatedAt date time")
    void commentDtoTest_ValidateCreatingCommentDtoForCreatingCommentWithUpdatedAtDateTime() {
        LocalDateTime updatedAt = LocalDateTime.of(2021, 1, 1, 1, 1);
        CommentDto commentDto = initCommentDto(null, "test", 1L, updatedAt);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto, Create.class);

        assertEquals(1, violations.size());
        assertEquals("Updated time not required",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Create CommentDto for creating comment with all violations")
    void commentDtoTest_ValidateCreatingCommentDtoForCreatingCommentWithAllViolations() {
        LocalDateTime updatedAt = LocalDateTime.of(2021, 1, 1, 1, 1);
        CommentDto commentDto = initCommentDto(1L, "", null, updatedAt);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto, Create.class);

        assertEquals(4, violations.size());
        assertTrue(violations.stream()
                .map(ConstraintViolation::getMessage)
                .toList()
                .containsAll(List.of(
                        "Id not required",
                        "Content for comment is required, and cannot be empty or blank",
                        "Author id is required",
                        "Updated time not required")));
    }

    @Test
    @DisplayName("Create CommentDto for updating comment")
    void commentDtoTest_ValidateUpdatingCommentDtoForUpdatingComment() {
        CommentDto commentDto = initCommentDto(null, "test", null, null);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto, Update.class);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Create CommentDto for updating comment with id")
    void commentDtoTest_ValidateUpdatingCommentDtoForUpdatingCommentWithId() {
        CommentDto commentDto = initCommentDto(1L, "test", null, null);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto, Update.class);

        assertEquals(1, violations.size());
        assertEquals("Id not required", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Create CommentDto for updating comment with empty content")
    void commentDtoTest_ValidateCreatingCommentDtoForUpdatingCommentWithEmptyContent() {
        CommentDto commentDto = initCommentDto(null, "", null, null);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto, Update.class);

        assertEquals(1, violations.size());
        assertEquals("Content for comment is required, and cannot be empty or blank",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Create CommentDto for updating comment with blank content")
    void commentDtoTest_ValidateCreatingCommentDtoForUpdatingCommentWithBlankContent() {
        CommentDto commentDto = initCommentDto(null, "   ", null, null);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto, Update.class);

        assertEquals(1, violations.size());
        assertEquals("Content for comment is required, and cannot be empty or blank",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Create CommentDto for updating comment with null content")
    void commentDtoTest_ValidateCreatingCommentDtoForUpdatingCommentWithNullContent() {
        CommentDto commentDto = initCommentDto(null, null, null, null);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto, Update.class);

        assertEquals(1, violations.size());
        assertEquals("Content for comment is required, and cannot be empty or blank",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Create CommentDto for updating comment with length of content bigger than max length")
    void commentDtoTest_ValidateCreatingCommentDtoForUpdatingCommentWithLengthOfContentBiggerThanMaxLength() {
        String content = "a".repeat(BIGGER_THAN_MAX_LENGTH);
        CommentDto commentDto = initCommentDto(null, content, null, null);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto, Update.class);

        assertEquals(1, violations.size());
        assertEquals("Content length must not be more than 4096 symbols",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Create CommentDto for updating comment with author id")
    void commentDtoTest_ValidateCreatingCommentDtoForUpdatingCommentWithAuthorId() {
        CommentDto commentDto = initCommentDto(null, "test", 1L, null);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto, Update.class);

        assertEquals(1, violations.size());
        assertEquals("Author id cannot be updated", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Create CommentDto for updating comment with updatedAt date time")
    void commentDtoTest_ValidateCreatingCommentDtoForUpdatingCommentWithUpdatedAtDateTime() {
        LocalDateTime updatedAt = LocalDateTime.of(2021, 1, 1, 1, 1);
        CommentDto commentDto = initCommentDto(null, "test", null, updatedAt);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto, Update.class);

        assertEquals(1, violations.size());
        assertEquals("Updated time not required",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Create CommentDto for updating comment with all violations")
    void commentDtoTest_ValidateCreatingCommentDtoForUpdatingCommentWithAllViolations() {
        LocalDateTime updatedAt = LocalDateTime.of(2021, 1, 1, 1, 1);
        CommentDto commentDto = initCommentDto(1L, "", 1L, updatedAt);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto, Update.class);

        assertEquals(4, violations.size());
        assertTrue(violations.stream()
                .map(ConstraintViolation::getMessage)
                .toList()
                .containsAll(List.of(
                        "Id not required",
                        "Content for comment is required, and cannot be empty or blank",
                        "Author id cannot be updated",
                        "Updated time not required")));
    }

    private CommentDto initCommentDto(Long id, String content, Long authorId, LocalDateTime updatedAt) {
        return CommentDto.builder()
                .id(id)
                .content(content)
                .authorId(authorId)
                .updatedAt(updatedAt)
                .build();
    }
}
