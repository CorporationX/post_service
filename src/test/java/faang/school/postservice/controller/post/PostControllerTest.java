package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {
    @InjectMocks
    private PostController postController;
    @Mock
    private PostService postService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testCreateDraftSuccessful() {
        PostDto postDto = PostDto.builder()
                .authorId(1L)
                .content("Hello, world!")
                .build();
        postController.createDraft(postDto);

    }

    @Test
    void publishPost() {
    }

    @Test
    void updatePost() {
    }

    @Test
    void removePostSoftly() {
    }

    @Test
    void getPostById() {
    }

    @Test
    void getPostDraftsByAuthorId() {
    }

    @Test
    void getPostDraftsByProjectId() {
    }
}