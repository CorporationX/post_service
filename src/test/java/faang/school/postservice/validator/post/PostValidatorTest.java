package faang.school.postservice.validator.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.PostValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostValidatorTest {

    @InjectMocks
    private PostValidator postValidator;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private PostRepository postRepository;

    private PostDto postDto;
    private final long postId = 1;

    @BeforeEach
    void setUp() {
        postDto = new PostDto();
    }

    @Test
    void testValidateCreateWithTwoAuthors() {
        postDto.setAuthorId(123L);
        postDto.setProjectId(444L);

        assertThrows(PostValidationException.class, () -> postValidator.validateCreate(postDto));
    }

    @Test
    void testValidateCreateWithoutAuthors() {
        assertThrows(PostValidationException.class, () -> postValidator.validateCreate(postDto));
    }

    @Test
    void testCheckIfAuthorExistsWithProjectId() {
        postDto.setProjectId(1L);

        when(projectServiceClient.getProject(postDto.getProjectId())).thenReturn(new ProjectDto());

        boolean result = postValidator.checkIfAuthorExists(postDto);

        assertTrue(result);

    }

    @Test
    void testCheckIfAuthorExistsWithAuthorId() {
        postDto.setAuthorId(1L);

        when(userServiceClient.getUser(postDto.getAuthorId())).thenReturn(new UserDto());

        boolean result = postValidator.checkIfAuthorExists(postDto);

        assertTrue(result);
    }

    @Test
    void testCheckIfAuthorExistsWhenDontExists() {
        postDto.setAuthorId(1L);
        postDto.setProjectId(1L);

        when(projectServiceClient.getProject(postDto.getProjectId())).thenReturn(null);
        when(userServiceClient.getUser(postDto.getAuthorId())).thenReturn(null);

        boolean result = postValidator.checkIfAuthorExists(postDto);

        assertFalse(result);
    }

    @Test
    void testValidatePublishWhenOptionalIsEmpty() {
        Optional<Post> post = Optional.empty();

        assertThrows(PostValidationException.class, () -> postValidator.validatePublish(post));
    }

    @Test
    void testValidatePublishWhenPostAlreadyPublished() {
        Post post = new Post();
        post.setPublished(true);
        Optional<Post> postOptional = Optional.of(post);

        assertThrows(PostValidationException.class, () -> postValidator.validatePublish(postOptional));
    }

    @Test
    void testValidateUpdateWhenPostNotExists() {
        Optional<Post> postOptional = Optional.empty();

        when(postRepository.findById(postId)).thenReturn(postOptional);

        assertThrows(PostValidationException.class, () -> postValidator.validateUpdate(postId, postDto));
    }

    @Test
    void testValidateUpdateWhenTryingChangeAuthor() {
        postDto.setAuthorId(3L);
        Post post = new Post();
        post.setAuthorId(1L);
        Optional<Post> postOptional = Optional.of(post);

        when(postRepository.findById(postId)).thenReturn(postOptional);

        assertThrows(PostValidationException.class, () -> postValidator.validateUpdate(postId, postDto));
    }
}