package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.HashtagService;
import faang.school.postservice.service.PostService;
import faang.school.postservice.util.exception.DataValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {
    @Mock
    private HashtagService hashtagService;

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    private MockMvc mockMvc;
    private PostDto postDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
        postDto = PostDto.builder().id(1L).content("content").build();
    }

    @Test
    public void testGetPostsByHashtag() throws Exception {
        String hashtag = "exampleHashtag";
        List<PostDto> mockPosts = List.of(postDto);
        when(hashtagService.getPostByHashtag(hashtag)).thenReturn(mockPosts);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/byhashtag/" + hashtag))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(postDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].content").value(postDto.getContent()));

        verify(hashtagService).getPostByHashtag(hashtag);
    }

    @Test
    public void testGetPostsByHashtagOverflowException() throws Exception {
        String longHashtag = "x".repeat(300);

        assertThrows(
                DataValidationException.class,
                () -> postController.getByHashtag(longHashtag)
        );

        verify(hashtagService, never()).getPostByHashtag(longHashtag);
    }
}
