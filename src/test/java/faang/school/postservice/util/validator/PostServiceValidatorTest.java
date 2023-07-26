package faang.school.postservice.util.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.exception.CreatePostException;
import faang.school.postservice.util.exception.PublishPostException;
import faang.school.postservice.util.exception.UpdatePostException;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PostServiceValidatorTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceValidator validator;

    private PostDto postDto;

    @BeforeEach
    void setUp() {
        postDto = PostDto.builder()
                .id(1L)
                .authorId(1L)
                .build();
    }

    @Test
    void validateToAdd_BothProjectAndAuthorExist_ShouldThrowException() {
        postDto.setProjectId(1L);

        CreatePostException e = Assert.assertThrows(CreatePostException.class, () -> {
            validator.validateToAdd(postDto);
        });
        Assertions.assertEquals("There is should be only one author", e.getMessage());
    }

    @Test
    void validateToAdd_BothProjectAndAuthorAreNull_ShouldThrowException() {
        postDto.setAuthorId(null);
        postDto.setProjectId(null);

        CreatePostException e = Assert.assertThrows(CreatePostException.class, () -> {
            validator.validateToAdd(postDto);
        });
        Assertions.assertEquals("There is should be only one author", e.getMessage());
    }

    @Test
    void validateToAdd_ByAuthor_ShouldSave() {
        validator.validateToAdd(postDto);

        Mockito.verify(userServiceClient, Mockito.times(1)).getUser(postDto.getAuthorId());
    }

    @Test
    void validateToAdd_ByProject_ShouldSave() {
        postDto.setAuthorId(null);
        postDto.setProjectId(1L);

        validator.validateToAdd(postDto);

        Mockito.verify(projectServiceClient, Mockito.times(1)).getProject(postDto.getProjectId());
    }

    @Test
    void validateToPublish_PostNotFound_ShouldThrowException() {
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.empty());

        PublishPostException e = Assert.assertThrows(PublishPostException.class, () -> {
            validator.validateToPublish(1L);
        });
        Assertions.assertEquals("Post not found", e.getMessage());
    }

    @Test
    void validateToPublish_PostIsPublished_ShouldThrowException() {
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(Post.builder().published(true).build()));

        PublishPostException e = Assert.assertThrows(PublishPostException.class, () -> {
            validator.validateToPublish(1L);
        });
        Assertions.assertEquals("Post is already published", e.getMessage());
    }

    @Test
    void validateToPublish_PostIsDeleted_ShouldThrowException() {
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(Post.builder().deleted(true).build()));

        PublishPostException e = Assert.assertThrows(PublishPostException.class, () -> {
            validator.validateToPublish(1L);
        });
        Assertions.assertEquals("Post is already deleted", e.getMessage());
    }

    @Test
    void validateToPublish_PostIsNotPublishedOrDeleted_ShouldNotThrowException() {
        Post post = Post.builder().published(false).deleted(false).build();
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        Assertions.assertEquals(post, validator.validateToPublish(1L));
    }

    @Test
    void validateToUpdate_PostNotFound_ShouldThrowException() {
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.empty());

        UpdatePostException e = Assert.assertThrows(UpdatePostException.class, () -> {
            validator.validateToUpdate(1L, "content");
        });
        Assertions.assertEquals("Post not found", e.getMessage());
    }

    @Test
    void validateToUpdate_PostIsDeleted_ShouldThrowException() {
        Post post = Post.builder().deleted(true).build();
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        UpdatePostException e = Assert.assertThrows(UpdatePostException.class, () -> {
            validator.validateToUpdate(1L, "content");
        });
        Assertions.assertEquals("Post is already deleted", e.getMessage());
    }

    @Test
    void validateToUpdate_PostIsNotPublished_ShouldThrowException() {
        Post post = Post.builder().published(false).build();
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        UpdatePostException e = Assert.assertThrows(UpdatePostException.class, () -> {
            validator.validateToUpdate(1L, "content");
        });
        Assertions.assertEquals("Post is in draft state. It can't be updated", e.getMessage());
    }

    @Test
    void validateToUpdate_ContentIsTheSame_ShouldThrowException() {
        Post post = Post.builder().published(true).content("content").build();
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        UpdatePostException e = Assert.assertThrows(UpdatePostException.class, () -> {
            validator.validateToUpdate(1L, "content");
        });
        Assertions.assertEquals("There is no changes to update", e.getMessage());
    }

    @Test
    void validateToUpdate_InputsAreCorrect_ShouldNotThrowException() {
        Post post = Post.builder().published(true).content("old content").build();
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        Assertions.assertDoesNotThrow(() -> validator.validateToUpdate(1L, "new content"));
    }
}
