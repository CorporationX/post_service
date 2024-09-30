package faang.school.postservice.validator;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.post.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostValidatorTest {

    @Spy
    private PostMapperImpl postMapper;

    private PostValidator postValidator;
    private PostDto postDto;

    @BeforeEach
    void setUp() {
        postValidator = new PostValidator();
        postDto = PostDto.builder()
                .id(1L)
                .content("Hello World")
                .authorId(1L)
                .projectId(1L)
                .build();
    }

    @Test
    void validatePost_ContentIsBlank() {
        postDto.setContent("   ");
        postDto.setAuthorId(null);
        postDto.setProjectId(null);

        Exception exception = assertThrows(DataValidationException.class, () -> {
            postValidator.validatePost(postDto);
        });

        String expectedMessage = "Either an author or a project is required";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void validatePost_isValidCreator_AuthorIdAndProjectIdIsNull() {
        postDto.setAuthorId(null);
        postDto.setProjectId(null);

        Exception exception = assertThrows(DataValidationException.class, () -> {
            postValidator.validatePost(postDto);
        });

        String expectedMessage = "Either an author or a project is required";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void validatePost_isValidCreator_AuthorIdAndProjectIdIsNotNull() {
        postDto.setAuthorId(1L);
        postDto.setProjectId(null);

        assertDoesNotThrow(() -> {
            postValidator.validatePost(postDto);
        });
    }

    @Test
    void validatePost_Valid() {
        postDto.setAuthorId(null);

        assertDoesNotThrow(() -> postValidator.validatePost(postDto));
    }

    private void assertPost() {
        String correctMessage = "PostDto is not valid";
        var exception = assertThrows(DataValidationException.class,
                () -> postValidator.validatePost(postDto));
        assertEquals(correctMessage, exception.getMessage());
    }

    @Test
    void validatePost_ExistsPost() {
        PostDto result = assertDoesNotThrow(
                () -> postValidator.validatePostWithReturnDto(postDto));

        assertEquals(postDto, result);
    }

    @Test
    void validatePost_NotExistsPost() {
        postDto = null;

        var exception = assertThrows(EntityNotFoundException.class,
                () -> postValidator.validatePostWithReturnDto(postDto));

        String expectedMessage = "There is no such post";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }
}