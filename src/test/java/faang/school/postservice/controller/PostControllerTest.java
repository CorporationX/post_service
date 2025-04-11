package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.exception.PostValidationException;
import faang.school.postservice.handler.GlobalExceptionHandler;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.doNothing;
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
@RequiredArgsConstructor
class PostControllerTest {
    private static final String BASE_URL = "/api/v1/posts";
    private static final long EXISTENT_POST_ID = 1L;
    private static final long NON_EXISTENT_POST_ID = 2L;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private PostDto inputDto;
    private PostDto returnedDto;

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(postController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        inputDto = PostDto.builder()
                .content("Test Content")
                .authorId(1L)
                .build();
        returnedDto = inputDto.toBuilder().id(EXISTENT_POST_ID).build();
    }

    @Test
    @DisplayName("Should return PostDto with ID when draft is created with valid input")
    void createDraftShouldCreateDraftWithValidInput() throws Exception {
        // Arrange
        when(postService.createDraft(inputDto)).thenReturn(returnedDto);

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/draft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(EXISTENT_POST_ID))
                .andExpect(jsonPath("$.content").value("Test Content"))
                .andExpect(jsonPath("$.authorId").value(1L));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when creating draft with invalid content")
    void createDraftShouldFailWithInvalidInput() throws Exception {
        String errorMessage = "Post content cannot be empty";
        PostDto invalidDto = inputDto.toBuilder().content(" ").build();
        when(postService.createDraft(invalidDto))
                .thenThrow(new PostValidationException(errorMessage));

        mockMvc.perform(post(BASE_URL + "/draft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.timestamp").exists());
    }


    @Test
    @DisplayName("Should return PostDto when post exists")
    void getPostShouldReturnPostIfExists() throws Exception {
        when(postService.getPost(1L)).thenReturn(returnedDto);

        mockMvc.perform(get(BASE_URL + "/{postId}", EXISTENT_POST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(EXISTENT_POST_ID))
                .andExpect(jsonPath("$.content").value("Test Content"))
                .andExpect(jsonPath("$.authorId").value(1L));
    }

    @Test
    @DisplayName("Should throw PostNotFoundException when post does not exist")
    void getPostShouldThrowExceptionIfPostDoesNotExist() throws Exception {
        String errorMessage = "Post with id %d does not exist".formatted(NON_EXISTENT_POST_ID);
        when(postService.getPost(NON_EXISTENT_POST_ID)).thenThrow(
                new PostNotFoundException(errorMessage)
        );

        mockMvc.perform(get(BASE_URL + "/{postId}", NON_EXISTENT_POST_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should throw PostNotFoundException when post is deleted")
    void getPostShouldThrowExceptionIfPostIsDeleted() throws Exception {
        String errorMessage = "Post with ID = %d is deleted".formatted(EXISTENT_POST_ID); //Post exist in DB but marked as deleted
        when(postService.getPost(EXISTENT_POST_ID)).thenThrow(new PostNotFoundException(errorMessage));

        mockMvc.perform(get(BASE_URL + "/{postId}", EXISTENT_POST_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.timestamp").exists());

    }

    @Test
    @DisplayName("Should publish post when it is a draft")
    void publishPostWhenDraft() throws Exception {
        returnedDto = inputDto.toBuilder().id(EXISTENT_POST_ID).published(true).build();
        when(postService.publishPost(EXISTENT_POST_ID)).thenReturn(returnedDto);

        mockMvc.perform(put(BASE_URL + "/{postId}/publish", EXISTENT_POST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(EXISTENT_POST_ID))
                .andExpect(jsonPath("$.content").value("Test Content"))
                .andExpect(jsonPath("$.authorId").value(1L))
                .andExpect(jsonPath("$.published").value(true));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when publishing a non-existent post")
    void publishPostWhenNonExistent() throws Exception {
        String errorMessage = "Post with id %d does not exist".formatted(NON_EXISTENT_POST_ID);
        when(postService.publishPost(NON_EXISTENT_POST_ID)).thenThrow(
                new PostValidationException(errorMessage)
        );
        mockMvc.perform(put(BASE_URL + "/{postId}/publish", NON_EXISTENT_POST_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should update post and return updated PostDto when post exists")
    void updatePostWhenExists() throws Exception {
        PostDto updatedPostDto = returnedDto.toBuilder().content("Updated Content").published(true).build();
        when(postService.updatePost(EXISTENT_POST_ID, "Updated Content")).thenReturn(updatedPostDto);

        mockMvc.perform(put(BASE_URL + "/{postId}", EXISTENT_POST_ID)
                        .content("Updated Content").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(EXISTENT_POST_ID))
                .andExpect(jsonPath("$.content").value("Updated Content"))
                .andExpect(jsonPath("$.authorId").value(1L))
                .andExpect(jsonPath("$.published").value(true));
    }

    @Test
    @DisplayName("Should return 404 Not Found when updating a non-existent post")
    void updatePostWhenNonExistent() throws Exception {
        String errorMessage = "Post with id %d does not exist".formatted(NON_EXISTENT_POST_ID);
        when(postService.updatePost(NON_EXISTENT_POST_ID, "Updated Content")).thenThrow(
                new PostNotFoundException(errorMessage)
        );

        mockMvc.perform(put(BASE_URL + "/{postId}", NON_EXISTENT_POST_ID)
                        .content("Updated Content").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.timestamp").exists());
    }


    @Test
    @DisplayName("Should soft delete post and return 204 No Content")
    void softDeletePost() throws Exception {
        doNothing().when(postService).softDeletePost(EXISTENT_POST_ID);

        mockMvc.perform(delete(BASE_URL + "/{postId}", EXISTENT_POST_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(postService, times(1)).softDeletePost(EXISTENT_POST_ID);
    }


    @Test
    @DisplayName("Get all drafts by authorId, sorted by date in descending order")
    void getAllDraftsByAuthorId() throws Exception {
        long authorId = 1L;

        List<PostDto> drafts = List.of(
                PostDto.builder()
                        .id(2L)
                        .content("Draft 2")
                        .authorId(authorId)
                        .published(false)
                        .createdAt(LocalDateTime.of(2020, 4, 1, 12, 0, 0))
                        .build(),
                PostDto.builder()
                        .id(1L)
                        .content("Draft 1")
                        .authorId(authorId)
                        .published(false)
                        .createdAt(LocalDateTime.of(2020, 3, 31, 12, 0, 0))
                        .build()
        );
        when(postService.getAllDraftsByAuthorId(authorId)).thenReturn(drafts);

        mockMvc.perform(get(BASE_URL + "/draft/by-author/{authorId}", authorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].content").value("Draft 2"))
                .andExpect(jsonPath("$[0].authorId").value(authorId))
                .andExpect(jsonPath("$[0].createdAt").value("2020-04-01T12:00:00"))
                .andExpect(jsonPath("$[1].id").value(1L))
                .andExpect(jsonPath("$[1].content").value("Draft 1"))
                .andExpect(jsonPath("$[1].authorId").value(authorId))
                .andExpect(jsonPath("$[1].createdAt").value("2020-03-31T12:00:00")
                );
    }

    @Test
    @DisplayName("Get all drafts by projectId, sorted by date in descending order")
    void getAllDraftsByProjectId() throws Exception {
        long projectId = 10L;
        List<PostDto> drafts = List.of(
                PostDto.builder()
                        .id(2L)
                        .content("Draft 2")
                        .projectId(projectId)
                        .published(false)
                        .createdAt(LocalDateTime.of(2020, 4, 1, 12, 0, 0))
                        .build(),
                PostDto.builder()
                        .id(1L)
                        .content("Draft 1")
                        .projectId(projectId)
                        .published(false)
                        .createdAt(LocalDateTime.of(2020, 3, 31, 12, 0, 0))
                        .build()
        );
        when(postService.getAllDraftsByProjectId(projectId)).thenReturn(drafts);

        mockMvc.perform(get(BASE_URL + "/draft/by-project/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].content").value("Draft 2"))
                .andExpect(jsonPath("$[0].projectId").value(projectId))
                .andExpect(jsonPath("$[0].createdAt").value("2020-04-01T12:00:00"))
                .andExpect(jsonPath("$[1].id").value(1L))
                .andExpect(jsonPath("$[1].content").value("Draft 1"))
                .andExpect(jsonPath("$[1].projectId").value(projectId))
                .andExpect(jsonPath("$[1].createdAt").value("2020-03-31T12:00:00"));
    }

    @Test
    @DisplayName("Get all posts by authorId, sorted by date in descending order")
    void getAllPostsByAuthorId() throws Exception {
        long authorId = 1L;
        List<PostDto> posts = List.of(
                PostDto.builder()
                        .id(2L)
                        .content("Post 2")
                        .authorId(authorId)
                        .published(true)
                        .createdAt(LocalDateTime.of(2023, 12, 1, 10, 0, 0))
                        .build(),
                PostDto.builder()
                        .id(1L)
                        .content("Post 1")
                        .authorId(authorId)
                        .published(true)
                        .createdAt(LocalDateTime.of(2023, 11, 30, 10, 0, 0))
                        .build()
        );
        when(postService.getAllPostsByAuthorId(authorId)).thenReturn(posts);

        mockMvc.perform(get(BASE_URL + "/by-author/{authorId}", authorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].content").value("Post 2"))
                .andExpect(jsonPath("$[0].authorId").value(authorId))
                .andExpect(jsonPath("$[0].published").value(true))
                .andExpect(jsonPath("$[0].createdAt").value("2023-12-01T10:00:00"))
                .andExpect(jsonPath("$[1].id").value(1L))
                .andExpect(jsonPath("$[1].content").value("Post 1"))
                .andExpect(jsonPath("$[1].authorId").value(authorId))
                .andExpect(jsonPath("$[1].published").value(true))
                .andExpect(jsonPath("$[1].createdAt").value("2023-11-30T10:00:00"));
    }

    @Test
    @DisplayName("Get all posts by projectId, sorted by date in descending order")
    void getAllPostsByProjectId() throws Exception {
        long projectId = 100L;
        List<PostDto> posts = List.of(
                PostDto.builder()
                        .id(2L)
                        .content("Project Post 2")
                        .projectId(projectId)
                        .published(true)
                        .createdAt(LocalDateTime.of(2023, 12, 1, 10, 0, 0))
                        .build(),
                PostDto.builder()
                        .id(1L)
                        .content("Project Post 1")
                        .projectId(projectId)
                        .published(true)
                        .createdAt(LocalDateTime.of(2023, 11, 30, 10, 0, 0))
                        .build()
        );
        when(postService.getAllPostsByProjectId(projectId)).thenReturn(posts);

        mockMvc.perform(get(BASE_URL + "/by-project/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].content").value("Project Post 2"))
                .andExpect(jsonPath("$[0].projectId").value(projectId))
                .andExpect(jsonPath("$[0].published").value(true))
                .andExpect(jsonPath("$[0].createdAt").value("2023-12-01T10:00:00"))
                .andExpect(jsonPath("$[1].id").value(1L))
                .andExpect(jsonPath("$[1].content").value("Project Post 1"))
                .andExpect(jsonPath("$[1].projectId").value(projectId))
                .andExpect(jsonPath("$[1].published").value(true))
                .andExpect(jsonPath("$[1].createdAt").value("2023-11-30T10:00:00"));
    }
}