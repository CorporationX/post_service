package faang.school.postservice.controller.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.util.post.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    @Mock
    private PostService postService;
    @Mock
    private PostValidator postValidator;
    @InjectMocks
    private PostController postController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "     ", "\t", "\n"})
    public void testCreateDraftContentIsInvalid(String content) throws Exception {
        final PostDto requestPostDto = new PostDto(
                null,
                content,
                1L,
                null,
                false,
                null,
                false

        );

        mockMvc.perform(post("/api/v1/posts/drafts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestPostDto)))
                .andExpect(status().isBadRequest());

        verify(postValidator, never()).validateIds(requestPostDto);
        verify(postService, never()).createDraft(requestPostDto);
    }

    @Test
    public void testCreateDraftIsSuccessful() throws Exception {
        final PostDto requestPostDto = new PostDto(
                null,
                "content",
                1L,
                null,
                false,
                null,
                false

        );
        final PostDto responsePostDto = new PostDto(
                5L,
                "content",
                1L,
                null,
                false,
                null,
                false
        );

        doNothing().when(postValidator).validateIds(requestPostDto);
        when(postService.createDraft(requestPostDto)).thenReturn(responsePostDto);

        mockMvc.perform(post("/api/v1/posts/drafts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestPostDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.content", is("content")))
                .andExpect(jsonPath("$.authorId", is(1)))
                .andExpect(jsonPath("$.projectId", nullValue()))
                .andExpect(jsonPath("$.published", is(false)))
                .andExpect(jsonPath("$.publishedAt", nullValue()))
                .andExpect(jsonPath("$.deleted", is(false)));

        verify(postValidator, times(1)).validateIds(requestPostDto);
        verify(postService, times(1)).createDraft(requestPostDto);
    }

    @Test
    public void testPublishPostIsSuccessful() throws Exception {
        final long postId = 5L;
        final PostDto responsePostDto = new PostDto(
                postId,
                "content",
                1L,
                null,
                true,
                LocalDateTime.now(),
                false
        );

        when(postService.publishPost(postId)).thenReturn(responsePostDto);

        mockMvc.perform(post("/api/v1/posts/drafts/{postId}", postId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.content", is("content")))
                .andExpect(jsonPath("$.authorId", is(1)))
                .andExpect(jsonPath("$.projectId", nullValue()))
                .andExpect(jsonPath("$.published", is(true)))
                .andExpect(jsonPath("$.publishedAt", notNullValue()))
                .andExpect(jsonPath("$.deleted", is(false)));

        verify(postService, times(1)).publishPost(postId);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "     ", "\t", "\n"})
    public void testUpdatePostContentIsInvalid(String content) throws Exception {
        final long postId = 5L;
        final PostDto requestPostDto = new PostDto(
                postId,
                content,
                1L,
                null,
                false,
                null,
                false

        );

        mockMvc.perform(put("/api/v1/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestPostDto)))
                .andExpect(status().isBadRequest());

        verify(postValidator, never()).validateIds(requestPostDto);
        verify(postService, never()).updatePost(postId, requestPostDto);
    }

    @Test
    public void testUpdatePostIsSuccessful() throws Exception {
        final long postId = 5L;
        final PostDto requestPostDto = new PostDto(
                postId,
                "new content",
                1L,
                null,
                false,
                null,
                false
        );
        final PostDto responsePostDto = new PostDto(
                postId,
                "new content",
                1L,
                null,
                false,
                null,
                false
        );

        doNothing().when(postValidator).validateIds(requestPostDto);
        when(postService.updatePost(postId, requestPostDto)).thenReturn(responsePostDto);

        mockMvc.perform(put("/api/v1/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestPostDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.content", is("new content")))
                .andExpect(jsonPath("$.authorId", is(1)))
                .andExpect(jsonPath("$.projectId", nullValue()))
                .andExpect(jsonPath("$.published", is(false)))
                .andExpect(jsonPath("$.publishedAt", nullValue()))
                .andExpect(jsonPath("$.deleted", is(false)));

        verify(postValidator, times(1)).validateIds(requestPostDto);
        verify(postService, times(1)).updatePost(postId, requestPostDto);
    }

    @Test
    public void testDeletePostIsSuccessful() throws Exception {
        final long postId = 5L;
        final PostDto responsePostDto = new PostDto(
                postId,
                "content",
                1L,
                null,
                false,
                null,
                true
        );

        when(postService.deletePost(postId)).thenReturn(responsePostDto);

        mockMvc.perform(delete("/api/v1/posts/{postId}", postId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.content", is("content")))
                .andExpect(jsonPath("$.authorId", is(1)))
                .andExpect(jsonPath("$.projectId", nullValue()))
                .andExpect(jsonPath("$.published", is(false)))
                .andExpect(jsonPath("$.publishedAt", nullValue()))
                .andExpect(jsonPath("$.deleted", is(true)));

        verify(postService, times(1)).deletePost(postId);
    }

    @Test
    public void testFindPostByIdIsSuccessful() throws Exception {
        final long postId = 5L;
        final PostDto responsePostDto = new PostDto(
                postId,
                "content",
                1L,
                null,
                false,
                null,
                false
        );

        when(postService.findPostById(postId)).thenReturn(responsePostDto);

        mockMvc.perform(get("/api/v1/posts/{postId}", postId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.content", is("content")))
                .andExpect(jsonPath("$.authorId", is(1)))
                .andExpect(jsonPath("$.projectId", nullValue()))
                .andExpect(jsonPath("$.published", is(false)))
                .andExpect(jsonPath("$.publishedAt", nullValue()))
                .andExpect(jsonPath("$.deleted", is(false)));

        verify(postService, times(1)).findPostById(postId);
    }

    @Test
    public void testFindDraftsByAuthorId() throws Exception {
        final long authorId = 1L;
        final List<PostDto> response = getDtosByAuthorId();

        when(postService.findDraftsByAuthorId(authorId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/posts/drafts/authors/{authorId}", authorId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].authorId", is(1)))
                .andExpect(jsonPath("$.[1].authorId", is(1)));

        verify(postService, times(1)).findDraftsByAuthorId(authorId);
    }

    @Test
    public void testFindDraftsByProjectId() throws Exception {
        final long projectId = 1L;
        final List<PostDto> response = getDtosByProjectId();

        when(postService.findDraftsByProjectId(projectId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/posts/drafts/projects/{projectId}", projectId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].projectId", is(1)))
                .andExpect(jsonPath("$.[1].projectId", is(1)));

        verify(postService, times(1)).findDraftsByProjectId(projectId);
    }

    @Test
    public void testFindPostsByAuthorId() throws Exception {
        final long authorId = 1L;
        final List<PostDto> response = getDtosByAuthorId();

        when(postService.findPostsByAuthorId(authorId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/posts/authors/{authorId}", authorId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].authorId", is(1)))
                .andExpect(jsonPath("$.[1].authorId", is(1)));

        verify(postService, times(1)).findPostsByAuthorId(authorId);
    }

    @Test
    public void testFindPostsByProjectId() throws Exception {
        final long projectId = 1L;
        final List<PostDto> response = getDtosByProjectId();

        when(postService.findPostsByProjectId(projectId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/posts/projects/{projectId}", projectId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].projectId", is(1)))
                .andExpect(jsonPath("$.[1].projectId", is(1)));

        verify(postService, times(1)).findPostsByProjectId(projectId);
    }

    private List<PostDto> getDtosByAuthorId() {
        final PostDto firstPostDto = new PostDto(
                1L,
                "content",
                1L,
                null,
                false,
                null,
                false
        );
        final PostDto secondPostDto = new PostDto(
                2L,
                "content",
                1L,
                null,
                false,
                null,
                false
        );
        return List.of(firstPostDto, secondPostDto);
    }

    private List<PostDto> getDtosByProjectId() {
        final PostDto firstPostDto = new PostDto(
                1L,
                "content",
                null,
                1L,
                false,
                null,
                false
        );
        final PostDto secondPostDto = new PostDto(
                2L,
                "content",
                null,
                1L,
                false,
                null,
                false
        );
        return List.of(firstPostDto, secondPostDto);
    }
}
