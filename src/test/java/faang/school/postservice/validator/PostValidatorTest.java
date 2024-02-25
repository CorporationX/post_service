package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PostValidatorTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostValidator postValidator;

    @Test
    public void testValidatePostAuthorAndProjectExists() {
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setAuthorId(1L);
        postDto.setProjectId(1L);

        Mockito.when(postRepository.existsById(postDto.getId())).thenReturn(false);

        Assertions.assertThrows(DataValidationException.class, () ->
                postValidator.validatePost(postDto));
    }

    @Test
    public void testValidateOwnerAuthorAndProjectNonExists() {
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setAuthorId(null);
        postDto.setProjectId(null);

        Mockito.when(postRepository.existsById(postDto.getId())).thenReturn(false);

        Assertions.assertThrows(DataValidationException.class, () ->
                postValidator.validatePost(postDto));
    }

    @Test
    public void testValidatePostSuccess() {
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setAuthorId(1L);
        postDto.setProjectId(null);

        Mockito.when(postRepository.existsById(postDto.getId())).thenReturn(false);

        Assertions.assertDoesNotThrow(() -> postValidator.validatePost(postDto));
    }

    @Test
    public void testValidatePostByProjectSuccess() {
        long ownerId = 2L;
        Post post = new Post();
        post.setId(1L);
        post.setAuthorId(null);
        post.setProjectId(ownerId);

        Optional<Post> postOpt = Optional.of(post);
        Mockito.when(postRepository.findById(1L)).thenReturn(postOpt);

        Assertions.assertDoesNotThrow(() -> postValidator.validatePostByOwner(1L, ownerId));
    }

    @Test
    public void testValidatePostByAuthorSuccess() {
        long ownerId = 1L;
        Post post = new Post();
        post.setId(1L);
        post.setAuthorId(ownerId);
        post.setProjectId(null);

        Optional<Post> postOpt = Optional.of(post);
        Mockito.when(postRepository.findById(1L)).thenReturn(postOpt);

        Assertions.assertDoesNotThrow(() -> postValidator.validatePostByOwner(1L, ownerId));
    }

    @Test
    public void testValidatePostByOwnerFailed() {
        long ownerId = 1L;
        Post post = new Post();
        post.setId(1L);
        post.setAuthorId(2L);
        post.setProjectId(null);

        Optional<Post> postOpt = Optional.of(post);
        Mockito.when(postRepository.findById(1L)).thenReturn(postOpt);

        Assertions.assertThrows(DataValidationException.class,
                () -> postValidator.validatePostByOwner(1L, ownerId));
    }

    @Test
    public void testValidatePostOwnerExistsSuccess() {
        PostDto postDto = new PostDto();
        postDto.setAuthorId(1L);

        Assertions.assertDoesNotThrow(() -> postValidator.validatePostOwnerExists(postDto));
    }

    @Test
    public void testValidatePostOwnerExistsFailed() {
        PostDto postDto = new PostDto();
        postDto.setAuthorId(null);

        Assertions.assertThrows(NullPointerException.class,
                () -> postValidator.validatePostOwnerExists(postDto));
    }

    @Test
    public void validatePostExistsSuccess() {
        long postId = 1L;
        Mockito.when(postRepository.existsById(postId)).thenReturn(false);

        Assertions.assertDoesNotThrow(() -> postValidator.validatePostExists(postId));
    }

    @Test
    public void validatePostExistsFailed() {
        long postId = 1L;
        Mockito.when(postRepository.existsById(postId)).thenReturn(true);

        Assertions.assertThrows(DataValidationException.class,
                () -> postValidator.validatePostExists(postId));
    }
}
