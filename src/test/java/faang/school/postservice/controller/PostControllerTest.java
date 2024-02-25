package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private PostController postController;

    private PostDto postDto;

    @BeforeEach
    public void init() {
        userContext.setUserId(1L);
        postDto = new PostDto();
    }

    @Test
    public void testCreatePostDraft() {
        postController.createPostDraft(postDto);
        Mockito.verify(postService, Mockito.times(1))
                .createPostDraft(postDto);
    }

    @Test
    public void testPublishPost() {
        postController.publishPost(1L);
        Mockito.verify(postService, Mockito.times(1))
                .publishPost(1L, userContext.getUserId());
    }

    @Test
    public void testUpdatePost() {
        postController.updatePost(1L, postDto);
        Mockito.verify(postService, Mockito.times(1))
                .updatePost(1L, userContext.getUserId(), postDto);
    }

    @Test
    public void testDeletePost() {
        postController.deletePost(1L);
        Mockito.verify(postService, Mockito.times(1))
                .deletePost(1L, userContext.getUserId());
    }

    @Test
    public void testGetAuthorDrafts() {
        postController.getAuthorDrafts(1L);
        Mockito.verify(postService, Mockito.times(1))
                .getAuthorDrafts(1L);
    }

    @Test
    public void testGetProjectDrafts() {
        postController.getProjectDrafts(1L);
        Mockito.verify(postService, Mockito.times(1))
                .getProjectDrafts(1L);
    }

    @Test
    public void testGetAuthorPosts() {
        postController.getAuthorPosts(1L);
        Mockito.verify(postService, Mockito.times(1))
                .getAuthorPosts(1L);
    }

    @Test
    public void testGetProjectPosts() {
        postController.getProjectPosts(1L);
        Mockito.verify(postService, Mockito.times(1))
                .getProjectPosts(1L);
    }

    @Test
    public void testPostById() {
        postController.getPostById(1L);
        Mockito.verify(postService, Mockito.times(1))
                .getPostById(1L);
    }
}
