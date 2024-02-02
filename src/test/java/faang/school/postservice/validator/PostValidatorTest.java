package faang.school.postservice.validator;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostValidatorTest {

    private final PostValidator postValidator = new PostValidator();

     PostDto postDto = new PostDto();
    Post post = new Post();

    @BeforeEach
    void setUp() {
        postDto.setId(1L);
        post.setId(1L);
    }

    @Test
    void validateIsNotPublishedTest() {
        post.setPublished(true);
        try {
            postValidator.validateIsNotPublished(post);
        } catch (DataValidationException e) {
            assertEquals("Пост уже опубликован", e.getMessage());
        }
    }

    @Test
    void validateTwoAuthorsTest() {
        postDto.setAuthorId(1L);
        postDto.setProjectId(2L);
        try {
            postValidator.validateAuthorCount(postDto);
        } catch (DataValidationException e) {
            assertEquals("У поста должен быть только один автор", e.getMessage());
        }
    }

    @Test
    void validateNoneAuthorsTest() {
        try {
            postValidator.validateAuthorCount(postDto);
        } catch (DataValidationException e) {
            assertEquals("У поста должен быть автор", e.getMessage());
        }
    }

    @Test
    void validateContentExistsTest() {
        try {
            postValidator.validateContentExists(postDto);
        } catch (DataValidationException e) {
            assertEquals("Пост не может быть пустым", e.getMessage());
        }
    }

    @Test
    void validateAuthorExistsTest() {
        UserDto userDto = null;
        ProjectDto projectDto = null;
        try {
            postValidator.validateAuthorExists(userDto, projectDto);
        } catch (DataValidationException e) {
            assertEquals("Такого автора не существует", e.getMessage());
        }
    }

    @Test
    void validateIdExistsTest() {
        postDto.setId(null);
        try {
            postValidator.validateIdExists(postDto);
        } catch (DataValidationException e) {
            assertEquals("Такого поста не существует", e.getMessage());
        }
    }

    @Test
    void validateAuthorChangedTest() {
        postDto.setAuthorId(1L);
        post.setAuthorId(2L);
        try {
            postValidator.validateCreatorNotChanged(postDto, post);
        } catch (DataValidationException e) {
            assertEquals("Создатель поста не может быть изменен", e.getMessage());
        }
    }

    @Test
    void validateProjectChangedTest() {
        postDto.setAuthorId(1L);
        post.setAuthorId(1L);
        postDto.setProjectId(1L);
        post.setProjectId(2L);
        try {
            postValidator.validateCreatorNotChanged(postDto, post);
        } catch (DataValidationException e) {
            assertEquals("Создатель поста не может быть изменен", e.getMessage());
        }
    }
}