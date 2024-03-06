package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {
    @InjectMocks
    private PostController postController;
    @Mock
    private PostService postService;
    private PostDto postDto;
    private long ID = 1L;
    private long NO_VALID_ID = -5;

    @BeforeEach
    void setUp() {
        postDto = PostDto.builder()
                .authorId(1L)
                .build();
    }

    @Test
    void testCreateDraftSuccessful() {
        postDto.setContent("Hello, world!");
        postController.createDraft(postDto);
        Mockito.verify(postService).createDraft(postDto);
    }

    @Test
    void testPublishPostSuccessful() {
        postController.publishPost(ID);
        Mockito.verify(postService).publish(ID);
    }

    @Test
    void testUpdatePostSuccessful() {
        postDto.setId(5L);
        postDto.setContent("Hello");
        postController.updatePost(postDto);
        Mockito.verify(postService).update(postDto);
    }

    @Test
    void testRemovePostSoftlySuccessful() {
        postController.removePostSoftly(ID);
        Mockito.verify(postService).deletePost(ID);
    }

    @Test
    void testGetPostByIdSuccessful() {
        postController.getPostById(ID);
        Mockito.verify(postService).getPostById(ID);
    }

    @Test
    void testGetPostDraftsByAuthorIdSuccessful() {
        postController.getDraftsByAuthorId(ID);
        Mockito.verify(postService).getDraftsByAuthorId(ID);
    }

    @Test
    void testGetPostDraftsByProjectIdSuccessful() {
        postController.getDraftsByProjectId(ID);
        Mockito.verify(postService).getDraftsByProjectId(ID);
    }

    @Test
    void testGetPublishedPostsByAuthorId() {
        postController.getPostsByAuthorId(ID);
        Mockito.verify(postService).getPublishedPostsByAuthorId(ID);
    }

    @Test
    void testGetPublishedPostsByProjectId() {
        postController.getPostsByProjectId(ID);
        Mockito.verify(postService).getPublishedPostsByProjectId(ID);
    }
}