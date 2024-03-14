package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {
    @Mock
    private PostService postService;
    @Mock
    private PostValidator postValidator;
    @InjectMocks
    private PostController postController;


    @Test
    void createDraftPostWithNoAuthorTest() {
        PostDto postDto = new PostDto();
        Mockito.doThrow(new DataValidationException("У поста должен быть автор")).when(postValidator).validateAuthorCount(postDto);
        assertThrows(DataValidationException.class, () -> postController.createDraftPost(postDto, null));
    }

    @Test
    void createDraftPostWithTwoAuthorsTest() {
        PostDto postDto = new PostDto();
        postDto.setAuthorId(1L);
        postDto.setProjectId(1L);
        Mockito.doThrow(new DataValidationException("У поста должен быть только один автор")).when(postValidator).validateAuthorCount(postDto);
        assertThrows(DataValidationException.class, () -> postController.createDraftPost(postDto, null));
    }

    @Test
    void createDraftPostWithEmptyContent() {
        PostDto postDto = new PostDto();
        postDto.setAuthorId(1L);
        Mockito.doThrow(new DataValidationException("Пост не может быть пустым")).when(postValidator).validateContentExists(postDto.getContent());
        assertThrows(DataValidationException.class, () -> postController.createDraftPost(postDto, null));

        postDto.setContent("");
        Mockito.doThrow(new DataValidationException("Пост не может быть пустым")).when(postValidator).validateContentExists(postDto.getContent());
        assertThrows(DataValidationException.class, () -> postController.createDraftPost(postDto, null));
    }

    @Test
    void shouldCreateDraftPost() {
        PostDto postDto = new PostDto();
        postDto.setAuthorId(1L);
        postDto.setContent("test");
        postController.createDraftPost(postDto, null);
        Mockito.verify(postService, Mockito.times(1)).createDraftPost(postDto, null);
    }

    @Test
    void updatePostWithEmptyContentTest() {
        UpdatePostDto postDto = new UpdatePostDto();
        postDto.setContent("");
        doThrow(new DataValidationException("Пост не может быть пустым"))
                .when(postValidator).validateContentExists(postDto.getContent());
        assertThrows(DataValidationException.class,
                () -> postController.updatePost(postDto, 1L, null));
    }

    @Test
    void updateCorrectPostTest() {
        UpdatePostDto postDto = new UpdatePostDto();
        postDto.setContent("test");
        postController.updatePost(postDto, 1L, null);
        Mockito.verify(postService, Mockito.times(1)).updatePost(postDto, 1L, null);
    }

    @Test
    void shouldDeletePost() {
        postController.deletePost(1L);
        verify(postService, times(1)).deletePost(1L);
    }
}