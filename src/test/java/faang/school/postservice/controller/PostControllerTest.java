package faang.school.postservice.controller;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.EmptyContentInPostException;
import faang.school.postservice.exception.IncorrectIdException;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class PostControllerTest {
    @Mock
    private PostService postService;
    @InjectMocks
    private PostController postController;
    private MockMvc mockMvc;
    private PostDto incorrectPostDto;
    private PostDto correctPostDto;
    private final String POST_CONTENT = "some content for test";
    private final Long CORRECT_ID = 1L;
    private final long INCORRECT_ID = 0;

    @BeforeEach
    void setUp() {
        incorrectPostDto = PostDto.builder()
                .content("   ")
                .build();
        correctPostDto = PostDto.builder()
                .content(POST_CONTENT)
                .authorId(CORRECT_ID)
                .cratedAt(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS))
                .build();
    }

    @Test
    void testCreateDaftPostWithEmptyContent() {
        assertThrows(EmptyContentInPostException.class, () -> postController.createDraftPost(incorrectPostDto));
    }

    @Test
    void testCreateDaftPostWithoutAuthor() {
        incorrectPostDto.setContent("post");
        assertThrows(IncorrectIdException.class, () -> postController.createDraftPost(incorrectPostDto));
    }

    @Test
    void testCreateDaftPost() {
        postController.createDraftPost(correctPostDto);
        verify(postService, times(1)).crateDraftPost(correctPostDto);
    }

    @Test
    void testPublishPost() {
        postController.publishPost(CORRECT_ID);
        verify(postService, times(1)).publishPost(CORRECT_ID);
    }
}