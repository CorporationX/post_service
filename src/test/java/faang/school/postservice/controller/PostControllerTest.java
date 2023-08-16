package faang.school.postservice.controller;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
        incorrectPostDto = PostDto.builder()
                .content("   ")
                .build();
        correctPostDto = PostDto.builder()
                .content(POST_CONTENT)
                .authorId(CORRECT_ID)
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS))
                .build();
    }

    @Test
    void testCreateDaftPost() {
        postController.createDraftPost(correctPostDto);
        verify(postService).crateDraftPost(correctPostDto);
    }

    @Test
    void testPublishPost() {
        postController.publishPost(CORRECT_ID);
        verify(postService).publishPost(CORRECT_ID);
    }

    @Test
    void testUpdatePost() {
        correctPostDto.setId(CORRECT_ID);
        postController.updatePost(correctPostDto);
        verify(postService).updatePost(correctPostDto);
    }

    @Test
    void testSoftDelete() {
        postController.softDelete(CORRECT_ID);
        verify(postService).softDelete(CORRECT_ID);
    }

    @Test
    void testGetPost() {
        postController.getPost(CORRECT_ID);
        verify(postService).getPost(CORRECT_ID);
    }

    @Test
    void testGetUserDrafts() {
        postController.getUserDrafts(CORRECT_ID);
        verify(postService).getUserDrafts(CORRECT_ID);
    }

    @Test
    void testGetProjectDrafts() {
        postController.getProjectDrafts(CORRECT_ID);
        verify(postService).getProjectDrafts(CORRECT_ID);
    }

    @Test
    void testGetUserPosts() {
        postController.getUserPosts(CORRECT_ID);
        verify(postService).getUserPosts(CORRECT_ID);
    }

    @Test
    void testGetProjectPosts() {
        postController.getProjectPosts(CORRECT_ID);
        verify(postService).getProjectPosts(CORRECT_ID);
    }
}