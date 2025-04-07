package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Spy
    private PostMapperImpl postMapper;

    @InjectMocks
    private PostServiceImpl postService;

    private PostDto postDto;
    private Post post;

    @BeforeEach
    public void setUp() {
        postDto = PostDto.builder()
                .id(1L)
                .content("Test content")
                .authorId(1L)
                .published(false)
                .deleted(false)
                .build();

        post = Post.builder()
                .id(1L)
                .content("Test content")
                .authorId(1L)
                .published(false)
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void testCreateDraft() {
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostDto result = postService.createDraft(postDto);

        assertNotNull(result);
        assertEquals(postDto.getContent(), result.getContent());
        assertEquals(postDto.getAuthorId(), result.getAuthorId());
        verify(postMapper).toEntity(postDto);
        verify(postRepository).save(any(Post.class));
        verify(postMapper).toDto(post);
    }

    @Test
    public void testPublishPost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        PostDto result = postService.publishPost(1L);

        assertNotNull(result);
        assertTrue(post.isPublished());
        assertNotNull(post.getPublishedAt());
        verify(postRepository).findById(1L);
        verify(postRepository).save(post);
        verify(postMapper).toDto(post);
    }

    @Test
    public void testPublishPostAlreadyPublished() {
        post.setPublished(true);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThrows(IllegalStateException.class, () -> postService.publishPost(1L));
        verify(postRepository).findById(1L);
        verify(postRepository, never()).save(post);
    }

    @Test
    public void testUpdatePost() {
        PostDto updatedPostDto = PostDto.builder()
                .id(1L)
                .content("Updated content")
                .authorId(1L)
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        PostDto result = postService.updatePost(1L, updatedPostDto);

        assertNotNull(result);
        assertEquals("Updated content", result.getContent());
        verify(postRepository).findById(1L);
        verify(postRepository).save(post);
        verify(postMapper).toDto(post);
    }

    @Test
    public void testUpdatePostChangeAuthor() {
        PostDto updatedPostDto = PostDto.builder()
                .id(1L)
                .content("Updated content")
                .authorId(2L)
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThrows(IllegalArgumentException.class, () -> postService.updatePost(1L, updatedPostDto));
        verify(postRepository).findById(1L);
        verify(postRepository, never()).save(post);
    }

    @Test
    public void testSoftDelete() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        PostDto result = postService.softDelete(1L);

        assertNotNull(result);
        assertTrue(post.isDeleted());
        verify(postRepository).findById(1L);
        verify(postRepository).save(post);
        verify(postMapper).toDto(post);
    }

    @Test
    public void testGetPostById() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        PostDto result = postService.getPostById(1L);

        assertNotNull(result);
        assertEquals(postDto.getContent(), result.getContent());
        assertEquals(postDto.getAuthorId(), result.getAuthorId());
        verify(postRepository).findById(1L);
        verify(postMapper).toDto(post);
    }

    @Test
    public void testGetPostByIdNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> postService.getPostById(1L));
        verify(postRepository).findById(1L);
    }

    @Test
    public void testGetAllDraftsByAuthorId() {
        when(postRepository.findByAuthorId(1L)).thenReturn(List.of(post));

        List<PostDto> result = postService.getAllDraftsByAuthorId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(postDto.getContent(), result.get(0).getContent());
        verify(postRepository).findByAuthorId(1L);
        verify(postMapper).toDto(post);
    }

    @Test
    public void testGetAllDraftsByProjectId() {
        when(postRepository.findByProjectId(1L)).thenReturn(List.of(post));

        List<PostDto> result = postService.getAllDraftsByProjectId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(postDto.getContent(), result.get(0).getContent());
        verify(postRepository).findByProjectId(1L);
        verify(postMapper).toDto(post);
    }

    @Test
    public void testGetAllPublishedPostsByAuthorId() {
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        when(postRepository.findByAuthorId(1L)).thenReturn(List.of(post));

        List<PostDto> result = postService.getAllPublishedPostsByAuthorId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(postDto.getContent(), result.get(0).getContent());
        verify(postRepository).findByAuthorId(1L);
        verify(postMapper).toDto(post);
    }

    @Test
    public void testGetAllPublishedPostsByProjectId() {
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        when(postRepository.findByProjectId(1L)).thenReturn(List.of(post));

        List<PostDto> result = postService.getAllPublishedPostsByProjectId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(postDto.getContent(), result.get(0).getContent());
        verify(postRepository).findByProjectId(1L);
        verify(postMapper).toDto(post);
    }

    @Test
    void testGetPostEntryByIdSuccessfulFetch() {
        long postId = 1L;
        Post post = new Post();
        post.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post result = postService.getPostEntryById(postId);

        assertNotNull(result);
        assertEquals(postId, result.getId());
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void testGetPostEntryByIdPostNotFound() {
        long postId = 2L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> postService.getPostEntryById(postId)
        );
        assertEquals("Post not found", exception.getMessage());
    }

    @Test
    public void testFindPostByIdThrowPostNotFoundException() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.findPostById(1L));
    }

    @Test
    public void testFindPostById() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Post actualResult = postService.findPostById(1L);

        assertNotNull(actualResult);
        assertEquals(postDto.getContent(), actualResult.getContent());
        assertEquals(postDto.getAuthorId(), actualResult.getAuthorId());
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    public void testRemoveTagsFromPost() {
        postService.removeTagsFromPost(1L, List.of(1L, 2L));

        verify(postRepository, times(1)).deleteTagsFromPost(1L, List.of(1L, 2L));
    }
}