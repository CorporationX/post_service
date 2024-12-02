package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PostControllerV1Test {

    private MockMvc mockMvc;
    @Mock
    private PostService postService;
    @InjectMocks
    private PostControllerV1 postControllerV1;

    private PostDto postDto;
    private List<PostDto> postDtoList;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postControllerV1).build();
        postDto = PostDto.builder()
                .id(1L)
                .authorId(1L)
                .content("content")
                .published(false)
                .build();
        PostDto secondPostDto = PostDto.builder()
                .id(2L)
                .build();
        postDtoList = List.of(postDto, secondPostDto);
    }

    @Test
    public void testGetPost() throws Exception {
        when(postService.getPostDtoById(1L)).thenReturn(postDto);

        mockMvc.perform(get("/api/v1/post/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.authorId").value(1L))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.published").value(false));
        verify(postService, times(1)).getPostDtoById(1L);
    }

    @Test
    public void testPublishPost() throws Exception {
        when(postService.publishPost(1L)).thenReturn(postDto);

        mockMvc.perform(put("/api/v1/post/publish/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.authorId").value(1L))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.published").value(false));
        verify(postService, times(1)).publishPost(1L);
    }

    @Test
    public void testDeletePost() throws Exception {
        when(postService.deletePost(1L)).thenReturn(postDto);

        mockMvc.perform(delete("/api/v1/post/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.authorId").value(1L))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.published").value(false));
        verify(postService, times(1)).deletePost(1L);
    }

    @Test
    public void testGetAllDraftNotDeletedPostsByUserId() throws Exception {
        when(postService.getAllDraftNotDeletedPostsByUserId(1L)).thenReturn(postDtoList);

        mockMvc.perform(get("/api/v1/post/drafts/byUser/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].authorId").value(1L))
                .andExpect(jsonPath("$[0].content").value("content"))
                .andExpect(jsonPath("$[0].published").value(false))
                .andExpect(jsonPath("$[1].id").value(2L));
        verify(postService, times(1)).getAllDraftNotDeletedPostsByUserId(1L);
    }

    @Test
    public void testGetAllDraftNotDeletedPostsByProjectId() throws Exception {
        when(postService.getAllDraftNotDeletedPostsByProjectId(1L)).thenReturn(postDtoList);

        mockMvc.perform(get("/api/v1/post/drafts/byProject/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].authorId").value(1L))
                .andExpect(jsonPath("$[0].content").value("content"))
                .andExpect(jsonPath("$[0].published").value(false))
                .andExpect(jsonPath("$[1].id").value(2L));
        verify(postService, times(1)).getAllDraftNotDeletedPostsByProjectId(1L);
    }

    @Test
    public void testGetAllPublishedNotDeletedPostsByUserId() throws Exception {
        when(postService.getAllPublishedNotDeletedPostsByUserId(1L)).thenReturn(postDtoList);

        mockMvc.perform(get("/api/v1/post/publications/byUser/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].authorId").value(1L))
                .andExpect(jsonPath("$[0].content").value("content"))
                .andExpect(jsonPath("$[0].published").value(false))
                .andExpect(jsonPath("$[1].id").value(2L));
        verify(postService, times(1)).getAllPublishedNotDeletedPostsByUserId(1L);
    }

    @Test
    public void testGetAllPublishedNotDeletedPostsByProjectId() throws Exception {
        when(postService.getAllPublishedNotDeletedPostsByProjectId(1L)).thenReturn(postDtoList);

        mockMvc.perform(get("/api/v1/post/publications/byProject/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].authorId").value(1L))
                .andExpect(jsonPath("$[0].content").value("content"))
                .andExpect(jsonPath("$[0].published").value(false))
                .andExpect(jsonPath("$[1].id").value(2L));
        verify(postService, times(1)).getAllPublishedNotDeletedPostsByProjectId(1L);
    }
}
