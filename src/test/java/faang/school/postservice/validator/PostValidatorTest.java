package faang.school.postservice.validator;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.post.PostMapperImpl;
import faang.school.postservice.model.Post;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        assertPost();
    }

    @Test
    void validatePost_isValidCreator_AuthorIdAndProjectIdIsNull() {
        postDto.setAuthorId(null);
        postDto.setProjectId(null);

        assertPost();
    }

    @Test
    void validatePost_isValidCreator_AuthorIdAndProjectIdIsNotNull() {
        assertPost();
    }

    @Test
    void validatePost_Valid() {
        postDto.setAuthorId(null);

        assertDoesNotThrow(() -> postValidator.validatePost(postDto));
    }

    private void assertPost() {
        String correctMessage = "Either an author or a project is required";
        var exception = assertThrows(DataValidationException.class,
                () -> postValidator.validatePost(postDto));
        assertEquals(correctMessage, exception.getMessage());
    }

    @Test
    void validatePost_ExistsPost() {
        Long id = postDto.getId();
        Post post = postMapper.toEntity(postDto);
        Optional<Post> optionalPost = Optional.of(post);

        Post result = assertDoesNotThrow(
                () -> postValidator.validatePost(optionalPost, id));

        assertEquals(post, result);
    }

    @Test
    void validatePost_NotExistsPost() {
        Long id = postDto.getId();
        String correctMessage = "There is no post with ID " + id;
        Optional<Post> optionalPost = Optional.empty();

        var exception = assertThrows(EntityNotFoundException.class,
                () -> postValidator.validatePost(optionalPost, id));

        assertEquals(correctMessage, exception.getMessage());
    }
}