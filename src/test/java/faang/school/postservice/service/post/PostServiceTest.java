package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        when(postRepository.existsById(validPost.getId())).thenReturn(false);
        when(userServiceClient.getUser(validPost.getAuthorId())).thenReturn(userDto);
        when(projectServiceClient.getProject(validPost.getProjectId())).thenReturn(projectDto);

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
        String newContent = "Test";
        LocalDateTime newScheduledAt = LocalDateTime.now();

        when(postRepository.findById(validPost.getId())).thenReturn(Optional.of(validPost));

        postService.updatePost(validPost.getId(), newContent, newScheduledAt);

        assertEquals(newContent, validPost.getContent());
        assertEquals(newScheduledAt, validPost.getScheduledAt());
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


}
