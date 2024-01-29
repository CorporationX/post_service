package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
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

    @InjectMocks
    private PostController postController;

    private final PostDto postDto = new PostDto();

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
                .publishPost(1L);
    }

    @Test
    public void testUpdatePost() {
        postController.updatePost(1L, postDto);
        Mockito.verify(postService, Mockito.times(1))
                .updatePost(1L, postDto);
    }

    @Test
    public void testDeletePost() {
        postController.deletePost(1L);
        Mockito.verify(postService, Mockito.times(1))
                .deletePost(1L);
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


}
