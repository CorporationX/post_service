package faang.school.postservice.service.post;

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
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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
    void testValidatePublished() {
        // given
        boolean isNotPublished = container.published();

        Post isNotPublishedPost = Post.builder()
                .published(isNotPublished)
                .build();

        Post publishedPost = Post.builder()
                .published(!isNotPublished)
                .publishedAt(container.publishedAt())
                .build();

        // then
        Assertions.assertThrows(PostAlreadyPublishedException.class, () -> validator.validateBeforePublishing(publishedPost));
        assertDoesNotThrow(() -> validator.validateBeforePublishing(isNotPublishedPost));
    }

    @Test
    void testValidateDeleted() {
        // given
        boolean isNotDeleted = container.deleted();

        Post isNotDeletedPost = Post.builder()
                .deleted(isNotDeleted)
                .build();

        Post deletedPost = Post.builder()
                .deleted(!isNotDeleted)
                .updatedAt(container.updatedAt())
                .build();

        // then
        Assertions.assertThrows(PostAlreadyDeletedException.class, () -> validator.validateBeforeDeleting(deletedPost));
        assertDoesNotThrow(() -> validator.validateBeforeDeleting(isNotDeletedPost));
    }

    @Test
    void testValidateBeforeCreate() {
        // given
        PostDto dtoWOAuthors = PostDto.builder()
                .id(container.postId())
                .build();

        PostDto dtoWithTwoAuthors = PostDto.builder()
                .authorId(container.authorId())
                .projectId(container.projectId())
                .build();

        PostDto authorsDtoPost = PostDto.builder()
                .authorId(container.authorId())
                .build();

        PostDto projectsDtoPost = PostDto.builder()
                .projectId(container.projectId())
                .build();

        // then
        Assertions.assertThrows(PostWithoutAuthorException.class, () -> validator.validateBeforeCreate(dtoWOAuthors));
        Assertions.assertThrows(PostWithTwoAuthorsException.class, () -> validator.validateBeforeCreate(dtoWithTwoAuthors));
        assertDoesNotThrow(() -> validator.validateBeforeCreate(authorsDtoPost));
        assertDoesNotThrow(() -> validator.validateBeforeCreate(projectsDtoPost));
    }

    @Test
    void testValidateBeforeUpdate() {
        // given
        Long postId = container.postId();
        Long authorId = container.authorId();
        PostDto dtoWithChangeAuthor = PostDto.builder()
                .id(postId)
                .authorId(authorId + 1)
                .build();

        PostDto dtoWithAuthorIsProject = PostDto.builder()
                .id(postId)
                .projectId(container.projectId())
                .build();

        PostDto validDto = PostDto.builder()
                .id(postId)
                .authorId(authorId)
                .build();

        Post entity = Post.builder()
                .id(postId)
                .authorId(authorId)
                .build();

        // then
        Assertions.assertThrows(ImmutablePostDataException.class, () -> validator.validateBeforeUpdate(dtoWithChangeAuthor, entity));
        Assertions.assertThrows(ImmutablePostDataException.class, () -> validator.validateBeforeUpdate(dtoWithAuthorIsProject, entity));
        assertDoesNotThrow(() -> validator.validateBeforeUpdate(validDto, entity));
    }

}
