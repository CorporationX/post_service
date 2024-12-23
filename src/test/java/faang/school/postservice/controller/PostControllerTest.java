package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.ExternalServiceException;
import faang.school.postservice.exception.PostValidationException;
import faang.school.postservice.publisher.PostViewEventPublisher;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private UserContext userContext;

    @MockBean
    private PostViewEventPublisher postViewEventPublisher;


    @Test
    void testCreatePostDraftSuccessful() throws Exception {
        PostDto postDto = createPostDto(null, "New test post", 1L, null, false);

        when(postService.createPostDraft(postDto)).thenReturn(postDto);

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(postDto)));
    }

    @Test
    void testCreatePostDraftWithInvalidPostData() throws Exception {
        PostDto postDto = createPostDto(null, "New test post", 1L, 1L, false);

        doThrow(new PostValidationException("A post can be created by a user or a project"))
                .when(postService).createPostDraft(postDto);

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Post validation failure"))
                .andExpect(jsonPath("$.message").value("A post can be created by a user or a project"));
    }

    @Test
    void testCreatePostDraftWithFailedInterServiceCommunication() throws Exception {
        PostDto postDto = createPostDto(null, "New test post", 1L, null, false);

        doThrow(new ExternalServiceException("Failed to communicate with User Service. Please try again later."))
                .when(postService).createPostDraft(postDto);

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Interaction failure"))
                .andExpect(jsonPath("$.message").value("Failed to communicate with User Service. Please try again later."));
    }

    @Test
    void testPublishPostSuccessful() throws Exception {
        PostDto postDto = createPostDto(1L, "New test post", 1L, null, true);

        when(postService.publishPost(1L)).thenReturn(postDto);

        mockMvc.perform(patch("/posts/1/publish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(postDto)));
    }

    @Test
    void testPublishPostWithInvalidPostState() throws Exception {
        doThrow(new PostValidationException("The post is already published"))
                .when(postService).publishPost(1L);

        mockMvc.perform(patch("/posts/1/publish"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Post validation failure"))
                .andExpect(jsonPath("$.message").value("The post is already published"));
    }

    @Test
    void testUpdatePostSuccessful() throws Exception {
        PostDto postDto = createPostDto(1L, "Updated test post's content", 1L, null, false);

        when(postService.updatePost(1L, postDto)).thenReturn(postDto);

        mockMvc.perform(patch("/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(postDto)));
    }

    @Test
    void testUpdatePostWithInvalidPostContent() throws Exception {
        PostDto postDto = createPostDto(1L, "Updated test post's content", 1L, null, false);
        doThrow(new PostValidationException("Post content cannot be empty, post ID: " + postDto.getId()))
                .when(postService).updatePost(1L, postDto);

        mockMvc.perform(patch("/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Post validation failure"))
                .andExpect(jsonPath("$.message").value("Post content cannot be empty, post ID: " + postDto.getId()));
    }

    @Test
    void testSoftDeleteSuccessful() throws Exception {
        doNothing().when(postService).softDelete(1L);

        mockMvc.perform(delete("/posts/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testSoftDeletePostAlreadyDeleted() throws Exception {
        long postId = 1L;

        doThrow(new PostValidationException("Post with ID: " + postId + " is already deleted"))
                .when(postService).softDelete(postId);

        mockMvc.perform(delete("/posts/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Post validation failure"))
                .andExpect(jsonPath("$.message").value("Post with ID: " + postId + " is already deleted"));
    }

    @Test
    void testGetPostByIdSuccessful() throws Exception {
        Long postId = 1L;
        Long viewerId = 2L;
        PostDto postDto = createPostDto(postId, "Post content", 1L, null, false);

        when(postService.getPostById(postId)).thenReturn(postDto);

        mockMvc.perform(get("/posts/{postId}", postId)
                        .param("viewerId", viewerId.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(postDto)));
    }

    @Test
    void testGetAllPostDraftsByUserIdSuccessful() throws Exception {
        PostDto postDto = createPostDto(1L, "Post content", 1L, null, false);
        List<PostDto> postDrafts = List.of(postDto);

        when(postService.getAllPostDraftsByUserId(postDto.getAuthorId())).thenReturn(postDrafts);

        mockMvc.perform(get("/users/1/posts/drafts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(postDrafts)));
    }

    @Test
    void testGetAllPostDraftsByProjectIdSuccessful() throws Exception {
        PostDto postDto = createPostDto(1L, "Post content", null, 1L, false);
        List<PostDto> postDrafts = List.of(postDto);

        when(postService.getAllPostDraftsByProjectId(postDto.getProjectId())).thenReturn(postDrafts);

        mockMvc.perform(get("/projects/1/posts/drafts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(postDrafts)));
    }

    @Test
    void testGetAllPublishedPostsByUserIdSuccessful() throws Exception {
        PostDto postDto = createPostDto(1L, "Post content", 1L, null, true);
        List<PostDto> posts = List.of(postDto);

        when(postService.getAllPublishedPostsByUserId(postDto.getAuthorId())).thenReturn(posts);

        mockMvc.perform(get("/users/1/posts/published")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(posts)));
    }

    @Test
    void testGetAllPublishedPostsByProjectIdSuccessful() throws Exception {
        PostDto postDto = createPostDto(1L, "Post content", null, 1L, true);
        List<PostDto> posts = List.of(postDto);

        when(postService.getAllPublishedPostsByProjectId(postDto.getProjectId())).thenReturn(posts);

        mockMvc.perform(get("/projects/1/posts/published")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(posts)));
    }

    private PostDto createPostDto(Long id, String content, Long authorId, Long projectId, boolean published) {
        PostDto postDto = new PostDto();
        postDto.setId(id);
        postDto.setContent(content);
        postDto.setAuthorId(authorId);
        postDto.setProjectId(projectId);
        postDto.setPublished(published);
        return postDto;
    }
}