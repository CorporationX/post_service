package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {
    @InjectMocks
    private PostController postController;
    @Mock
    private PostService postService;
    @Mock
    private PostValidator postValidator;
    @Mock
    private PostDto postDtoWithEmptyAuthorIdAndProjectId;

    long authorId;
    long projectId;
    long postId;
    PostDto postDtoWithAuthorId;
    PostDto postDtoWithProjectId;

    @BeforeEach
    void setUp() {
        authorId = 1L;
        projectId = 1L;
        postId = 1L;
        postDtoWithAuthorId = PostDto.builder().content("author content").authorId(authorId).build();
        postDtoWithProjectId = PostDto.builder().content("project content").projectId(projectId).build();
        postDtoWithEmptyAuthorIdAndProjectId = PostDto.builder().content("content").build();
    }

    @Test
    void testCreatePostWithAuthorIdSuccess() {
        postController.createPost(postDtoWithAuthorId);
        verify(postService, Mockito.times(1)).createPost(postDtoWithAuthorId);
    }

    @Test
    void testCreatePostWithProjectIdSuccess() {
        postController.createPost(postDtoWithProjectId);
        verify(postService, Mockito.times(1)).createPost(postDtoWithProjectId);
    }

    @Test
    void testThrowExceptionWhenCreatePostWithoutAuthorIdOrProjectId() {
        String message = "AuthorId or ProjectId cannot be null";
        doThrow(new DataValidationException(message)).when(postValidator).validationOfPostCreatorIds(postDtoWithEmptyAuthorIdAndProjectId);
        assertEquals(message, assertThrows(DataValidationException.class,
                () -> postController.createPost(postDtoWithEmptyAuthorIdAndProjectId)).getMessage());
        verifyNoInteractions(postService);
    }

    @Test
    void testPublishPost() {
        postController.publishPost(postId);
        verify(postService, Mockito.times(1)).publishPost(postId);
    }

    @Test
    void testGetPost() {
        postController.getPost(1L);
        verify(postService, Mockito.times(1)).getPost(1L);
    }

    @Test
    void deletePost() {
        postController.deletePost(postDtoWithAuthorId.getId());
        verify(postService, Mockito.times(1)).softDeletePost(postDtoWithAuthorId.getId());
    }
}