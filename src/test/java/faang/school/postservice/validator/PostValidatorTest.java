package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDTO;
import faang.school.postservice.exception.DataUpdateException;
import faang.school.postservice.exception.RequiredOwnerException;
import faang.school.postservice.exception.SinglePostAuthorException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.adapter.PostRepositoryAdapter;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PostValidatorTest {

    @InjectMocks
    private PostValidator postValidator;

    @Mock
    UserServiceClient userServiceClient;

    @Mock
    ProjectServiceClient projectServiceClient;

    @Mock
    PostRepositoryAdapter postRepositoryAdapter;

    private final Post post = new Post();
    private PostDTO postDto = new PostDTO();


    @Test
    @DisplayName("Test must return exception when a post has not owner")
    void testNoPostOwner() {
        Assertions.assertThrows(RequiredOwnerException.class, () -> postValidator.validatedOwnerPost(postDto));
    }

    @Test
    @DisplayName("Test must return exception when post has owner project and user")
    void testShouldBeSingleOwnerPost() {
        postDto = PostDTO.builder()
                .authorId(1L)
                .projectId(2L)
                .build();

        Assertions.assertThrows(SinglePostAuthorException.class, () -> postValidator.validatedOwnerPost(postDto));
    }

    @Test
    @DisplayName("Test must call the method getUser(userId)")
    void testValidatedOwnerUser() {
        postDto = PostDTO.builder()
                .authorId(1L)
                .build();

        postValidator.validatedOwnerPost(postDto);

        Mockito.verify(userServiceClient, Mockito.times(1)).getUser(1L);
    }

    @Test
    @DisplayName("Test must call the method getProject(projectId)")
    void testValidatedOwnerProject() {
        postDto = PostDTO.builder()
                .projectId(1L)
                .build();

        postValidator.validatedOwnerPost(postDto);

        Mockito.verify(projectServiceClient, Mockito.times(1)).getProjectById(1L);
    }

    @Test
    @DisplayName("Test successful find post by id")
    void testFindPostById() {
        Mockito.when(postRepositoryAdapter.findById(1L)).thenReturn(Optional.of(post));

        postValidator.findPostWithId(1L);

        Mockito.verify(postRepositoryAdapter, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("Test must return exception when post not found")
    void testNotFoundPostById() {
        Mockito.when(postRepositoryAdapter.findById(10L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> postValidator.findPostWithId(10L));
    }

    @Test
    @DisplayName("Test must be exception when user want delete the author id of the post")
    void testDeleteAuthorId() {
        post.setAuthorId(1L);

        Assertions.assertThrows(DataUpdateException.class,
                () -> postValidator.validateAuthorForUpdate(post, postDto));
    }

    @Test
    @DisplayName("Test must be exception when user want delete the project id of the post")
    void testDeleteProjectAuthor() {
        post.setProjectId(1L);

        Assertions.assertThrows(DataUpdateException.class,
                () -> postValidator.validateAuthorForUpdate(post, postDto));
    }
}