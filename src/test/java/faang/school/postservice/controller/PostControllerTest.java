package faang.school.postservice.controller;

import faang.school.postservice.broker.producer.PostViewEventProducer;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.controller.post.PostController;
import faang.school.postservice.dto.post.PostCreateRequestDto;
import faang.school.postservice.dto.post.PostUpdateRequestDto;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {
    @Mock
    private PostService postServiceMock;
    @Mock
    private PostViewEventProducer postViewEventProducer;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private PostController postController;
    private PostCreateRequestDto validPostCreateRequestDto;
    private PostUpdateRequestDto validUpdatedPostRequestDto;

    @BeforeEach
    void setUp() {
        postController = new PostController(postServiceMock, postViewEventProducer, userContext);
        validPostCreateRequestDto = PostCreateRequestDto.builder()
                .content("test content")
                .authorId(111L)
                .projectId(222L)
                .build();
        validUpdatedPostRequestDto = PostUpdateRequestDto.builder()
                .content("test content updated")
                .build();
    }

    @Test
    @DisplayName("Test create draft")
    void testCreatePostDraftByValidDto() {
        postController.createPostDraft(validPostCreateRequestDto);
        Mockito.verify(postServiceMock, Mockito.times(1))
                .createPostDraft(validPostCreateRequestDto);
    }

    @Test
    @DisplayName("Test publish post")
    void testPublishPostDraft() {
        Long postId = 123L;
        postController.publishPostDraft(postId);
        Mockito.verify(postServiceMock, Mockito.times(1))
                .publishPostDraft(postId);
    }

    @Test
    @DisplayName("Test update post")
    void testUpdatePost() {
        Long postId = 1L;
        postController.updatePost(postId, validUpdatedPostRequestDto);
        Mockito.verify(postServiceMock, Mockito.times(1))
                .updatePost(postId, validUpdatedPostRequestDto);
    }

    @Test
    @DisplayName("Test delete post")
    void testDeletePost() {
        Long postId = 123L;
        postController.deletePost(postId);
        Mockito.verify(postServiceMock, Mockito.times(1))
                .deletePost(postId);
    }

    @Test
    @DisplayName("Test get post")
    void testGetPost() {
        Long postId = 123L;
        postController.getPost(postId);
        Mockito.verify(postServiceMock, Mockito.times(1))
                .getPostWithCache(postId);
    }

    @Test
    @DisplayName("Test get posts ")
    void testGetProjectPostDrafts() {
        Long projectId = 123L;
        postController.getFilteredPosts(true, projectId, null);
        Mockito.verify(postServiceMock, Mockito.times(1))
                .findAllByFilter(Mockito.any());
    }
}