package faang.school.postservice.controller;

import faang.school.postservice.controller.post.PostController;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.anyString;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup(){
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
    }

    @Test
    void testGetNullHashtagThrows() throws Exception {
        assertThrows(DataValidationException.class,
                () -> postController.getPostsByHashtag(null));

        verify(postService, never()).getPostsByHashtag(anyString());
    }

    @Test
    void testGetEmptyHashtagThrows() {
        assertThrows(DataValidationException.class,
                () -> postController.getPostsByHashtag(""));

        verify(postService, never()).getPostsByHashtag(anyString());
    }

    @Test
    void testGetHashtagOk() {
        postController.getPostsByHashtag("hashtag");

        verify(postService).getPostsByHashtag(anyString());
    }
}
