package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.post.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostValidator postValidator;

    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @Mock
    private PostRepository postRepository;

    private PostDto examplePostDto;
    private Post examplePost;

    @BeforeEach
    void setUp() {
        examplePostDto = PostDto.builder()
                .id(1L)
                .authorId(1L)
                .content("Content")
                .title("Title")
                .build();

        examplePost = Post.builder()
                .id(1L)
                .authorId(1L)
                .content("Content")
                .title("Title")
                .build();
    }

    /*@Test
    void createDraftPost_shouldReturnPostDto() {
        // Arrange
        when(postMapper.toEntity(any(PostDto.class))).thenReturn(examplePost);
        when(postRepository.save(any(Post.class))).thenReturn(examplePost);

        // Act
        PostDto result = postService.createDraftPost(examplePostDto);

        // Assert
        assertEquals(examplePostDto, result);
        assertFalse(examplePost.isPublished());
        assertNotNull(examplePost.getCreatedAt());
        verify(postValidator, times(1)).createDraftPostValidator(examplePostDto);
        verify(postRepository, times(1)).save(examplePost);
    }*/

    @Test
    void publishPost_shouldReturnPostDto() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.ofNullable(examplePost));
        when(postRepository.save(any(Post.class))).thenReturn(examplePost);

        // Act
        PostDto result = postService.publishPost(examplePostDto);

        // Assert
        assertEquals(examplePostDto, result);
        assertTrue(examplePost.isPublished());
        assertNotNull(examplePost.getPublishedAt());
        verify(postRepository, times(1)).findById(1L);
        verify(postValidator, times(1)).publishPostValidator(examplePost);
        verify(postRepository, times(1)).save(examplePost);
    }

    @Test
    void updatePost_shouldReturnPostDto() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.ofNullable(examplePost));
        when(postRepository.save(any(Post.class))).thenReturn(examplePost);

        // Act
        PostDto result = postService.updatePost(examplePostDto);

        // Assert
        assertEquals(examplePostDto, result);
        assertNotNull(examplePost.getUpdatedAt());
        verify(postRepository, times(1)).findById(1L);
        verify(postValidator, times(1)).updatePostValidator(examplePost, examplePostDto);
        verify(postRepository, times(1)).save(examplePost);
    }

    @Test
    void softDeletePost_shouldReturnPostDto() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.ofNullable(examplePost));
        when(postRepository.save(any(Post.class))).thenReturn(examplePost);

        // Act
        PostDto result = postService.softDeletePost(1L);

        // Assert
        assertEquals(examplePostDto, result);
        assertFalse(examplePost.isPublished());
        assertTrue(examplePost.isDeleted());
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(examplePost);
    }

    @Test
    void getPost_shouldReturnPostDto() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.ofNullable(examplePost));

        // Act
        PostDto result = postService.getPost(1L);

        // Assert
        assertEquals(examplePostDto, result);
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void getAllDraftsByAuthorId_shouldReturnListOfPostDto() {
        // Arrange
        examplePost = Post.builder()
                .id(1L)
                .authorId(1L)
                .content("Content")
                .title("Title")
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .published(false)
                .build();

        Iterable<Post> iterablePostList = List.of(examplePost);
        when(postRepository.findAll()).thenReturn(iterablePostList);

        // Act
        List<PostDto> result = postService.getAllDraftsByAuthorId(1L);

        // Assert
        List<PostDto> expectedResult = List.of(examplePostDto);
        assertEquals(expectedResult, result);
        verify(postValidator, times(1)).validateIfAuthorExists(1L);
    }

    @Test
    void getAllDraftsByProjectId_shouldReturnListOfEventDto() {
        // Arrange
        examplePost = Post.builder()
                .id(1L)
                .projectId(1L)
                .content("Content")
                .title("Title")
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .published(false)
                .build();

        examplePostDto = PostDto.builder()
                .id(1L)
                .projectId(1L)
                .content("Content")
                .title("Title")
                .build();

        Iterable<Post> iterablePostList = List.of(examplePost);
        when(postRepository.findAll()).thenReturn(iterablePostList);

        // Act
        List<PostDto> result = postService.getAllDraftsByProjectId(1L);

        // Assert
        List<PostDto> expectedResult = List.of(examplePostDto);
        assertEquals(expectedResult, result);
        verify(postValidator, times(1)).validateIfProjectExists(1L);
    }

    @Test
    void getAllPublishedPostsByAuthorId_shouldReturnListOfPostDto() {
        // Arrange
        examplePost = Post.builder()
                .id(1L)
                .authorId(1L)
                .content("Content")
                .title("Title")
                .createdAt(LocalDateTime.now().minusMinutes(2))
                .publishedAt(LocalDateTime.now())
                .deleted(false)
                .published(true)
                .build();

        Iterable<Post> iterablePostList = List.of(examplePost);
        when(postRepository.findAll()).thenReturn(iterablePostList);

        // Act
        List<PostDto> result = postService.getAllPublishedPostsByAuthorId(1L);

        // Assert
        List<PostDto> expectedResult = List.of(examplePostDto);
        assertEquals(expectedResult, result);
        verify(postValidator, times(1)).validateIfAuthorExists(1L);
    }

    @Test
    void getAllPublishedPostsByProjectId_shouldReturnListOfPostDto() {
        // Arrange
        examplePost = Post.builder()
                .id(1L)
                .projectId(1L)
                .content("Content")
                .title("Title")
                .createdAt(LocalDateTime.now().minusMinutes(2))
                .publishedAt(LocalDateTime.now())
                .deleted(false)
                .published(true)
                .build();

        examplePostDto = PostDto.builder()
                .id(1L)
                .projectId(1L)
                .content("Content")
                .title("Title")
                .build();

        Iterable<Post> iterablePostList = List.of(examplePost);
        when(postRepository.findAll()).thenReturn(iterablePostList);

        // Act
        List<PostDto> result = postService.getAllPublishedPostsByProjectId(1L);

        // Assert
        List<PostDto> expectedResult = List.of(examplePostDto);
        assertEquals(expectedResult, result);
        verify(postValidator, times(1)).validateIfProjectExists(1L);
    }
}
