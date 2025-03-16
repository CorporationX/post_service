package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDTO;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @InjectMocks
    private PostController postController;

    @Mock
    private PostService postService;

    private PostDTO draftAuthorUser;
    private PostDTO draftAuthorProject;

    private PostDTO postAuthorUser;
    private PostDTO postAuthorProject;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController)
                .build();

        objectMapper = new ObjectMapper();

        draftAuthorUser = PostDTO.builder()
                .id(1L)
                .content("Lose yourself")
                .authorId(1L)
                .build();

        draftAuthorProject = PostDTO.builder()
                .id(1L)
                .content("Hakuna matata")
                .projectId(1L)
                .build();

        postAuthorUser = PostDTO.builder()
                .id(1L)
                .content("Lose yourself")
                .authorId(1L)
                .published(true)
                .build();

        postAuthorProject = PostDTO.builder()
                .id(1L)
                .content("Hakuna matata")
                .projectId(1L)
                .published(true)
                .build();
    }

    @Test
    void createDraft() throws Exception {
        Mockito.when(postService.createDraft(any(PostDTO.class))).thenReturn(draftAuthorUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts/drafts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(draftAuthorUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Lose yourself"));

        Mockito.verify(postService, times(1)).createDraft(draftAuthorUser);
    }

    @Test
    void publishPost() throws Exception {
        Mockito.when(postService.publishPost(draftAuthorUser.id())).thenReturn(postAuthorUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postAuthorUser)))
                .andExpect(status().isOk());

        Mockito.verify(postService, times(1)).publishPost(1L);
    }

    @Test
    void updatePost() throws Exception {
        Mockito.when(postService.updatePost(postAuthorUser)).thenReturn(postAuthorUser);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postAuthorUser)))
                .andExpect(status().isOk());

        Mockito.verify(postService, times(1)).updatePost(postAuthorUser);
    }

    @Test
    void deletePost() throws Exception {
        Mockito.when(postService.deletePost(1L)).thenReturn(postAuthorUser);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postAuthorUser)))
                .andExpect(status().isOk());

        Mockito.verify(postService, times(1)).deletePost(1L);
    }

    @Test
    void getPostById() throws Exception {
        Mockito.when(postService.getPostById(1L)).thenReturn(postAuthorUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postAuthorUser)))
                .andExpect(status().isOk());

        Mockito.verify(postService, times(1)).getPostById(1L);
    }

    @Test
    void getAllDraftsByAuthorId() throws Exception {
        Mockito.when(postService.getAllDraftsByAuthorId(1L)).thenReturn(List.of(draftAuthorUser));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/posts/drafts/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(draftAuthorUser))))
                .andExpect(status().isOk());

        Mockito.verify(postService, times(1)).getAllDraftsByAuthorId(1L);
    }

    @Test
    void getAllDraftsByProjectId() throws Exception {
        Mockito.when(postService.getAllDraftsByProjectId(1L)).thenReturn(List.of(draftAuthorProject));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/posts/drafts/project/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(draftAuthorProject))))
                .andExpect(status().isOk());

        Mockito.verify(postService, times(1)).getAllDraftsByProjectId(1L);
    }

    @Test
    void getAllPostsByAuthorId() throws Exception {
        Mockito.when(postService.getAllPostsByAuthorId(1L)).thenReturn(List.of(postAuthorUser));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/posts/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(postAuthorUser))))
                .andExpect(status().isOk());

        Mockito.verify(postService, times(1)).getAllPostsByAuthorId(1L);
    }

    @Test
    void getAllPostsByProjectId() throws Exception {
        Mockito.when(postService.getAllPostsByProjectId(1L)).thenReturn(List.of(postAuthorProject));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/posts/project/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(postAuthorProject))))
                .andExpect(status().isOk());

        Mockito.verify(postService, times(1)).getAllPostsByProjectId(1L);
    }
}