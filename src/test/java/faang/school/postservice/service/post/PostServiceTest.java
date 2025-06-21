package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    private PostService postService;

    private final Post validPost =
            Post.builder()
                    .authorId(1L)
                    .projectId(1L)
                    .content("Test")
                    .published(false)
                    .deleted(false)
                    .build();

    @Test
    public void testCreatePostValid() {
        UserDto userDto = new UserDto(1L, "test", "test");
        ProjectDto projectDto = new ProjectDto(1L, "test");

        when(userContext.getUserId()).thenReturn(validPost.getAuthorId());
        when(userServiceClient.getUser(validPost.getAuthorId())).thenReturn(userDto);
        when(projectServiceClient.getProject(validPost.getProjectId())).thenReturn(projectDto);
        when(postRepository.findById(validPost.getId())).thenReturn(Optional.of(validPost));

        postService.createPost(validPost);

        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository, times(1)).save(captor.capture());

        Post savedPost = captor.getValue();
        assertEquals(validPost, savedPost);
    }

    @Test
    public void testPublishPostValid() {
        when(postRepository.findById(validPost.getId())).thenReturn(Optional.of(validPost));

        postService.publishPost(validPost.getId());

        assertTrue(validPost.isPublished());
        assertNotNull(validPost.getPublishedAt());
        verify(postRepository, times(1)).save(validPost);
    }

    @Test
    public void testUpdatePostValid() {
        Post post = Post.builder().content("Test").scheduledAt(LocalDateTime.now()).build();

        when(postRepository.findById(validPost.getId())).thenReturn(Optional.of(validPost));

        postService.updatePost(validPost.getId(), post);

        assertEquals(post.getContent(), validPost.getContent());
        assertEquals(post.getScheduledAt(), validPost.getScheduledAt());
        assertNotNull(validPost.getUpdatedAt());
        verify(postRepository, times(1)).save(validPost);
    }

    @Test
    public void testDeletePostValid() {

        when(postRepository.findById(validPost.getId())).thenReturn(Optional.of(validPost));

        postService.deletePost(validPost.getId());

        assertTrue(validPost.isDeleted());
        verify(postRepository, times(1)).save(validPost);
    }

    @Test
    void getAllUnpublishedPost() {
        when(postRepository.findAllUnpublishedPosts()).thenReturn(List.of(validPost));

        List<Post> result = postService.getAllUnpublishedPost();

        verify(postRepository).findAllUnpublishedPosts();
        assertEquals(validPost.getId(), result.get(0).getId());
    }

    @Test
    void updateCorrectedContentOfPost() {
        String correctedContent = "correctedContent";

        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        when(postRepository.save(any(Post.class))).thenReturn(any(Post.class));

        postService.updateCorrectedContentOfPost(validPost, correctedContent);

        verify(postRepository).save(captor.capture());
    }
}