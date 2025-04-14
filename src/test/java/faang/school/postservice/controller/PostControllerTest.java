package faang.school.postservice.controller;

import faang.school.postservice.service.PostCorrecter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {

    private static final String CHECK_SPELLING_SUCCESS = "Posts have been spell checked";

    @Mock
    private PostCorrecter postCorrecter;

    @InjectMocks
    private PostController postController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
    }

    @Test
    public void testCorrectUnpublishedPosts() throws Exception {
        doNothing().when(postCorrecter).correctUnpublishedPosts();

        mockMvc.perform(post("/posts/check-grammar"))
                .andExpect(status().isOk())
                .andExpect(content().string(CHECK_SPELLING_SUCCESS));

        verify(postCorrecter, times(1)).correctUnpublishedPosts();
    }
}
