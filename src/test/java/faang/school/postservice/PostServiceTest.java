package faang.school.postservice;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private UserContext userContext;

    @InjectMocks
    PostService postService;


    private Post post;

    @BeforeEach
    public void setUp() {
        post = new Post();
        post.setId(1L);
        post.setAuthorId(1L);
        post.setContent("Sample content");
        post.setPublished(false);
    }

    @Test
    public void testCreateDraftPost() {
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post result = postService.createDraftPost(post);
        assertFalse(result.isPublished(), "The post should be marked as draft (not published).");

        verify(postRepository, times(1)).save(post);
        assertNotNull(result);
    }

    @Test
    public void testCreateDraftPostCallsValidateUserExists() {
        post.setAuthorId(1L);
        post.setProjectId(null);

        postService.createDraftPost(post);

        verify(userContext, times(1)).setUserId(post.getAuthorId());
        verify(userServiceClient, times(1)).getUser(post.getAuthorId());
    }

    @Test
    public void testCreateDraftPostCallsValidateProjectExists() {
        post.setAuthorId(null);
        post.setProjectId(1L);

        postService.createDraftPost(post);

        verify(projectServiceClient, times(1)).getProject(post.getProjectId());
    }


    @Test
    public void testUpdatePost() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        postService.updatePost(post);

        verify(postRepository, times(1)).save(post);
    }


    @Test
    public void testDeletePost() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        postService.deletePost(post.getId());

        verify(postRepository, times(1)).save(post);
    }
}
