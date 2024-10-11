package faang.school.postservice.validator;

import faang.school.postservice.dto.post.PostDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PostValidatorTest {
    @InjectMocks
    private PostValidator postValidator;

    private PostDto postDtoNullContent;
    private PostDto postDtoBlankContent;
    private final long ANY_ID = 1L;
    private final String BLANK_CONTENT = "  ";

    @BeforeEach
    public void init() {
        postDtoNullContent = PostDto.builder()
                .id(ANY_ID)
                .content(null)
                .authorId(ANY_ID)
                .projectId(ANY_ID)
                .published(true)
                .publishedAt(LocalDateTime.now())
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .build();
        postDtoBlankContent = PostDto.builder()
                .id(ANY_ID)
                .content(BLANK_CONTENT)
                .authorId(ANY_ID)
                .projectId(ANY_ID)
                .published(true)
                .publishedAt(LocalDateTime.now())
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Data validation exception when post content is blank")
    void whenContentIsBlankThenException() {
        assertThrows(NullPointerException.class,
                () -> postValidator.validateBlankContent(postDtoBlankContent), "Post content can't be blank");
    }

    @Test
    @DisplayName("Data validation exception when post content is null")
    void whenContentIsNullThenException() {
        assertThrows(NullPointerException.class,
                () -> postValidator.validateBlankContent(postDtoNullContent), "Post content can't be blank");
    }
}