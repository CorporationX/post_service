package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServerValidatorTest {
    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceValidator postServiceValidator;

    private PostDto postDto;
    private Post post;
    private PostDto postDtoWithProjectOwner;
    private Post postWithProjectOwner;


    @BeforeEach
    void setUp() {
        postDto = PostDto.builder()
                .id(1L)
                .authorId(1L)
                .projectId(null)
                .build();

        post = Post.builder()
                .id(1L)
                .authorId(1L)
                .projectId(null)
                .published(false)
                .deleted(false)
                .build();

        postDtoWithProjectOwner = PostDto.builder()
                .id(1L)
                .authorId(null)
                .projectId(1L)
                .build();

        postWithProjectOwner = Post.builder()
                .id(1L)
                .authorId(null)
                .projectId(1L)
                .published(false)
                .deleted(false)
                .build();
    }

    @Test
    @DisplayName("Create post with both IDs null")
    void testCreatePostBothIdsNull() {
        postDto = postDto.builder().authorId(null).projectId(null).build();

        assertThrows(DataValidationException.class, () -> postServiceValidator.validateCreatePost(postDto));
    }

    @Test
    @DisplayName("Create post with both IDs not null")
    void testCreatePostBothIdsNotNull() {
        postDto = postDto.builder().authorId(1L).projectId(1L).build();

        assertThrows(DataValidationException.class, () -> postServiceValidator.validateCreatePost(postDto));
    }

    @Test
    @DisplayName("Create post with valid author ID")
    void testCreatePostValidAuthorId() {
        postDto = postDto.builder().authorId(1L).projectId(null).build();
        when(userServiceClient.getUserById(postDto.getAuthorId())).thenReturn(any(UserDto.class));
        postServiceValidator.validateCreatePost(postDto);

        verify(userServiceClient).getUserById(postDto.getAuthorId());
    }

    @Test
    @DisplayName("Create post with valid project ID")
    void testCreatePostValidProjectId() {
        postDto = postDto.builder().authorId(null).projectId(1L).build();
        when(projectServiceClient.getProject(postDto.getProjectId())).thenReturn(any(ProjectDto.class));
        postServiceValidator.validateCreatePost(postDto);

        verify(projectServiceClient).getProject(postDto.getProjectId());
    }

    @Test
    @DisplayName("Update post with different author ID")
    void testUpdatePostDifferentAuthorId() {
        postDto = PostDto.builder().id(1L).authorId(2L).build();

        assertThrows(DataValidationException.class, () -> postServiceValidator.validateUpdatePost(post, postDto));
    }

    @Test
    @DisplayName("Publish post when already published")
    void testPublishPostAlreadyPublished() {
        post = Post.builder().published(true).build();

        assertThrows(DataValidationException.class, () -> postServiceValidator.validatePublishPost(post));
    }

    @Test
    @DisplayName("Delete post when already deleted")
    void testDeletePostAlreadyDeleted() {
        post = Post.builder().deleted(true).build();

        assertThrows(DataValidationException.class, () -> postServiceValidator.validateDeletePost(post));
    }
}
