package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {
    @InjectMocks
    private PostController postController;

    @Mock
    private PostServiceImpl postService;

    private PostDto postDto;

    @BeforeEach
    void setUp() {
        postDto = PostDto.builder()
                .id(1L)
                .authorId(1L)
                .content("Content")
                .title("Title")
                .build();
    }


    // Those are testing if Controller works properly with Service
    @Test
    void testCreateDraftPost() {
        // Arrange
        when(postService.createDraftPost(postDto)).thenReturn(postDto);

        // Act
        PostDto result = postController.createDraftPost(postDto);

        // Assert
        assertEquals(postDto, result);
        verify(postService, times(1)).createDraftPost(postDto);
    }

    @Test
    void testPublishPost() {
        // Arrange
        when(postService.publishPost(postDto)).thenReturn(postDto);

        // Act
        PostDto result = postController.publishPost(postDto);

        // Assert
        assertEquals(postDto, result);
        verify(postService, times(1)).publishPost(postDto);
    }

    @Test
    void testUpdatePost() {
        // Arrange
        when(postService.updatePost(postDto)).thenReturn(postDto);

        // Act
        PostDto result = postController.updatePost(postDto);

        // Assert
        assertEquals(postDto, result);
        verify(postService, times(1)).updatePost(postDto);
    }

    @Test
    void testSoftDeletePost() {
        // Arrange
        when(postService.softDeletePost(1L)).thenReturn(postDto);

        // Act
        PostDto result = postController.softDeletePost(1L);

        // Assert
        assertEquals(postDto, result);
        verify(postService, times(1)).softDeletePost(1L);
    }

    @Test
    void testGetPost() {
        // Arrange
        when(postService.getPost(1L)).thenReturn(postDto);

        // Act
        PostDto result = postController.getPost(1L);

        // Assert
        assertEquals(postDto, result);
        verify(postService, times(1)).getPost(1L);
    }

    @Test
    void testGetAllDraftsByAuthorId() {
        // Arrange
        when(postService.getAllDraftsByAuthorId(1L)).thenReturn(List.of(postDto));

        // Act
        List<PostDto> result = postController.getAllDraftsByAuthorId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(postDto, result.get(0));
        verify(postService, times(1)).getAllDraftsByAuthorId(1L);
    }

    @Test
    void testGetAllPublishedPostsByAuthorId() {
        // Arrange
        when(postService.getAllPublishedPostsByAuthorId(1L)).thenReturn(List.of(postDto));

        // Act
        List<PostDto> result = postController.getAllPublishedPostsByAuthorId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(postDto, result.get(0));
        verify(postService, times(1)).getAllPublishedPostsByAuthorId(1L);
    }

    @Test
    void testGetAllPublishedPostsByProjectId() {
        // Arrange
        when(postService.getAllPublishedPostsByProjectId(1L)).thenReturn(List.of(postDto));

        // Act
        List<PostDto> result = postController.getAllPublishedPostsByProjectId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(postDto, result.get(0));
        verify(postService, times(1)).getAllPublishedPostsByProjectId(1L);
    }
}
