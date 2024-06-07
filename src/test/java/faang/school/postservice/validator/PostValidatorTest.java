package faang.school.postservice.validator;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.validation.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PostValidatorTest {

    private PostValidator validator;

    private PostDto postDto;
    private Post post;

    @BeforeEach
    void setUp() {
        validator = new PostValidator();
        postDto = PostDto.builder().content("qwe").authorId(1L).build();
        post = Post.builder().content("123").authorId(3L).published(true).build();
    }

    @Test
    void validateAuthorCountWithTwoAuthors() {
        postDto.setProjectId(2L);
        assertThrows(DataValidationException.class,
                () -> validator.validateAuthorCount(postDto));
    }

    @Test
    void validateAuthorCountAuthorsNotExists() {
        assertThrows(DataValidationException.class,
                () -> validator.validateAuthorCount(new PostDto()));
    }

    @Test
    void validateAuthorCount() {
        assertDoesNotThrow(() -> validator.validateAuthorCount(postDto));
    }

    @Test
    void validateAuthorNotExist() {
        UserDto author = null;
        ProjectDto project = null;
        assertThrows(DataValidationException.class,
                () -> validator.validateAuthorExist(author, project));
    }

    @Test
    void validateIsNotPublishedWithPublished() {
        assertThrows(DataValidationException.class, () -> validator.validateIsNotPublished(post));
    }

    @Test
    void validateChangeAuthorChangeUser() {
        assertThrows(DataValidationException.class,
                () -> validator.validateChangeAuthor(post, postDto));
    }

    @Test
    void validateChangeAuthorChangeProject() {
        postDto.setProjectId(1L);
        post.setProjectId(2L);

        assertThrows(DataValidationException.class,
                () -> validator.validateChangeAuthor(post, postDto));
    }

    @Test
    void validatePostExistEmptyList() {
        assertThrows(EntityNotFoundException.class, () -> validator.validatePostsExists(new ArrayList<>()));
    }

    @Test
    void validatePostExistWithNull() {
        List<Post> posts = null;
        assertThrows(EntityNotFoundException.class,
                () -> validator.validatePostsExists(posts));
    }
}
