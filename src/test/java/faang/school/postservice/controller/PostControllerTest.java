package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private PostController postController;

    private final PostDto postDto = PostDto.builder().id(1L).build();
    private List<MultipartFile> files;

    @BeforeEach
    public void init() {
        userContext.setUserId(1L);
        MultipartFile multipartFileMock = mock(MultipartFile.class);
        files = new ArrayList<>(List.of(multipartFileMock, multipartFileMock, multipartFileMock));

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

    @Test
    void testCreatePost () {
        when(postService.createPost(postDto, files)).thenReturn(postDto);

        PostDto postByController = postController.createPost(postDto, files);

        assertEquals (postDto, postByController);
        verify(postService, times(1)).createPost(postDto, files);
    }

    @Test
    void testUpdatePost () {
        Long postId = postDto.getId();

        when(postService.updatePost(postId, postDto, files)).thenReturn(postDto);

        PostDto postByController = postController.updatePost(postId, postDto, files);

        assertEquals (postDto, postByController);
        verify(postService, times(1)).updatePost(postId, postDto, files);
    }

    @Test
    void testGetPost () {
        Long postId = postDto.getId();

        when(postService.getPostDto(postId)).thenReturn(postDto);

        PostDto postByController = postController.getPost(postId);

        assertEquals (postDto, postByController);
        verify(postService, times(1)).getPostDto(postId);
    }
}
