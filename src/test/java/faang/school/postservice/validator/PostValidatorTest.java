package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.post.ImmutablePostDataException;
import faang.school.postservice.exception.post.PostAlreadyDeletedException;
import faang.school.postservice.exception.post.PostAlreadyPublishedException;
import faang.school.postservice.exception.post.PostWithoutAuthorException;
import faang.school.postservice.exception.post.PostWithTwoAuthorsException;
import faang.school.postservice.model.Post;
import faang.school.postservice.util.container.PostContainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class PostValidatorTest {
    @InjectMocks
    private PostValidator validator;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;

    private PostContainer container = new PostContainer();

    @Test
    void testValidateBeforePublishing() {
        Post post = Post.builder()
                .published(false)
                .deleted(false)
                .build();

        assertDoesNotThrow(() -> validator.validateBeforePublishing(post));
    }

    @Test
    void testValidateBeforePublishingWhenPublished() {
        Post publishedPost = Post.builder()
                .published(true)
                .build();

        assertThrows(PostAlreadyPublishedException.class, () -> validator.validateBeforePublishing(publishedPost));
    }

    @Test
    void testValidateBeforePublishingWhenDeleted() {
        Post deletedPost = Post.builder()
                .deleted(true)
                .build();

        assertThrows(PostAlreadyDeletedException.class, () -> validator.validateBeforePublishing(deletedPost));
    }

    @Test
    void testValidateBeforeUpdateAuthorOwn() {
        PostDto dto = PostDto.builder()
                .id(container.postId())
                .authorId(container.authorId())
                .build();
        Post entity = Post.builder()
                .id(container.postId())
                .authorId(container.authorId())
                .build();

        assertDoesNotThrow(() -> validator.validateBeforeUpdate(dto, entity));
    }

    @Test
    void testValidateBeforeUpdateProjectOwn() {
        PostDto dto = PostDto.builder()
                .id(container.postId())
                .authorId(container.projectId())
                .build();
        Post entity = Post.builder()
                .id(container.postId())
                .authorId(container.projectId())
                .build();

        assertDoesNotThrow(() -> validator.validateBeforeUpdate(dto, entity));
    }

    @Test
    void testValidateBeforeUpdateWhenAuthorChanged() {
        Long postId = container.postId();
        PostDto dto = PostDto.builder()
                .id(postId)
                .authorId(1L)
                .build();
        Post entity = Post.builder()
                .id(postId)
                .authorId(2L)
                .build();

        assertThrows(ImmutablePostDataException.class, () -> validator.validateBeforeUpdate(dto, entity));
    }

    @Test
    void testValidateBeforeUpdateWhenProjectChanged() {
        Long postId = container.postId();
        PostDto dto = PostDto.builder()
                .id(postId)
                .projectId(1L)
                .build();
        Post entity = Post.builder()
                .id(postId)
                .projectId(2L)
                .build();

        assertThrows(ImmutablePostDataException.class, () -> validator.validateBeforeUpdate(dto, entity));
    }

    @Test
    void testValidateBeforeCreateWithoutAuthor() {
        PostDto dtoWithoutAuthors = PostDto.builder()
                .id(container.postId())
                .build();

        assertThrows(PostWithoutAuthorException.class, () -> validator.validateBeforeCreate(dtoWithoutAuthors));
    }

    @Test
    void testValidateBeforeCreateWithTwoAuthors() {
        PostDto dtoWithTwoAuthors = PostDto.builder()
                .authorId(container.authorId())
                .projectId(container.projectId())
                .build();

        assertThrows(PostWithTwoAuthorsException.class, () -> validator.validateBeforeCreate(dtoWithTwoAuthors));
    }

    @Test
    void testValidateBeforeCreateWithAuthor() {
        PostDto postDtoByAuthor = PostDto.builder()
                .authorId(container.authorId())
                .build();

        assertDoesNotThrow(() -> validator.validateBeforeCreate(postDtoByAuthor));
    }

    @Test
    void testValidateBeforeCreateWithProject() {
        PostDto postDtoByProject = PostDto.builder()
                .projectId(container.projectId())
                .build();

        assertDoesNotThrow(() -> validator.validateBeforeCreate(postDtoByProject));
    }

    @Test
    void testValidateBeforeDeleting() {
        Post notDeletedPost = Post.builder()
                .deleted(false)
                .build();

        assertDoesNotThrow(() -> validator.validateBeforeDeleting(notDeletedPost));
    }

    @Test
    void testValidateBeforeDeletingWhenDeleted() {
        Post deletedPost = Post.builder()
                .deleted(true)
                .updatedAt(container.updatedAt())
                .build();

        assertThrows(PostAlreadyDeletedException.class, () -> validator.validateBeforeDeleting(deletedPost));
    }
}
