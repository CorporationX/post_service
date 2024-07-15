package faang.school.postservice.postServiceTest;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.PostServiceValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @InjectMocks
    private PostService postService;

    @Mock
    private PostMapper postMapper;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostServiceValidator postServiceValidator;

    private PostDto postDto;
    private Post post;
    private List<Post> draftPosts;
    private List<Post> publishedPosts;
    private List<PostDto> draftPostDtos;
    private List<PostDto> publishedPostDtos;

    @BeforeEach
    public void setUp() {
        postDto = new PostDto();
        post = new Post();
        Post draftPost1 = Post.builder()
                .id(1L)
                .content("Draft 1")
                .createdAt(LocalDateTime.now().minusDays(2))
                .deleted(false)
                .build();

        Post draftPost2 = Post.builder()
                .id(2L)
                .content("Draft 2")
                .createdAt(LocalDateTime.now().minusDays(1))
                .published(false)
                .build();

        Post publishedPost1 = Post.builder().id(3L)
                .content("Published 1")
                .publishedAt(LocalDateTime.now().minusDays(2))
                .createdAt(LocalDateTime.now().minusDays(3))
                .published(true)
                .build();

        Post publishedPost2 = Post.builder().id(2L)
                .content("Published 2")
                .publishedAt(LocalDateTime.now().minusDays(1))
                .createdAt(LocalDateTime.now().minusDays(4))
                .published(true)
                .build();

        draftPosts = Arrays.asList(draftPost1, draftPost2);
        publishedPosts = Arrays.asList(publishedPost1, publishedPost2);

        PostDto draftPostDto1 = PostDto.builder()
                .id(1L)
                .content("Draft 1")
                .createdAt(LocalDateTime.now().minusDays(2))
                .published(false)
                .build();
        PostDto draftPostDto2 = PostDto.builder()
                .id(2L)
                .content("Draft 2")
                .createdAt(LocalDateTime.now().minusDays(1))
                .createdAt(LocalDateTime.now().minusDays(3))
                .published(false)
                .build();
        PostDto publishedPostDto1 = PostDto.builder()
                .id(3L)
                .content("Published 1")
                .publishedAt(LocalDateTime.now().minusDays(2))
                .createdAt(LocalDateTime.now().minusDays(4))
                .published(true)
                .build();

        PostDto publishedPostDto2 = PostDto.builder()
                .id(4L)
                .content("Published 2")
                .publishedAt(LocalDateTime.now().minusDays(1))
                .published(true)
                .build();

        draftPostDtos = Arrays.asList(draftPostDto1, draftPostDto2);
        publishedPostDtos = Arrays.asList(publishedPostDto1, publishedPostDto2);
    }

    @Test
    public void testCreatePost() {
        doNothing().when(postServiceValidator).validateCreatePost(postDto);
        when(postMapper.toEntity(postDto)).thenReturn(post);
        postService.createPost(postDto);

        verify(postRepository, times(1)).save(post);
    }

    @Test
    public void testUpdatePost() {
        doNothing().when(postServiceValidator).validateUpdatePost(postDto);
        when(postMapper.toEntity(postDto)).thenReturn(post);
        postService.updatePost(postDto);

        verify(postRepository, times(1)).save(post);
    }

    @Test
    public void testPublishPost() {
        doNothing().when(postServiceValidator).validatePublishPost(postDto);
        when(postMapper.toEntity(postDto)).thenReturn(post);

        postService.publishPost(postDto);
        verify(postRepository, times(1)).save(post);
        assertTrue(postDto.isPublished());
    }

    @Test
    public void testDeletePostPostFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        doNothing().when(postServiceValidator).validateDeletePost(post);


        postService.deletePost(1L);
        verify(postRepository, times(1)).save(post);
        assertTrue(post.isDeleted());
        assertFalse(post.isPublished());
    }

    @Test
    public void testDeletePostPostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.deletePost(1L));
    }


    @Test
    public void testGetPostByPostIdPostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.getPostByPostId(1L));
    }

    @Test
    public void testGetPostByPostIdPostFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        postService.getPostByPostId(1L);

        verify(postMapper, times(1)).toDto(post);
    }

    @Test
    void getAllDraftPostsByUserId() {
        when(postRepository.findByAuthorId(1L)).thenReturn(draftPosts);
        when(postMapper.toDto(anyList())).thenReturn(draftPostDtos);

        List<PostDto> result = postService.getAllDraftPostsByUserId(1L);

        assertEquals(2, result.size());
        assertEquals(draftPostDtos, result);
    }

    @Test
    void getAllDraftPostsByProjectId() {
        when(postRepository.findByProjectId(1L)).thenReturn(draftPosts);
        when(postMapper.toDto(anyList())).thenReturn(draftPostDtos);

        List<PostDto> result = postService.getAllDraftPostsByProjectId(1L);

        assertEquals(2, result.size());
        assertEquals(draftPostDtos, result);
    }

    @Test
    void getAllPublishPostsByUserId() {
        when(postRepository.findByAuthorId(1L)).thenReturn(publishedPosts);
        when(postMapper.toDto(anyList())).thenReturn(publishedPostDtos);

        List<PostDto> result = postService.getAllPublishPostsByUserId(1L);

        assertEquals(2, result.size());
        assertEquals(publishedPostDtos, result);
    }

    @Test
    void getAllPublishPostsByProjectId() {
        when(postRepository.findByProjectId(1L)).thenReturn(publishedPosts);
        when(postMapper.toDto(anyList())).thenReturn(publishedPostDtos);

        List<PostDto> result = postService.getAllPublishPostsByProjectId(1L);

        assertEquals(2, result.size());
        assertEquals(publishedPostDtos, result);
    }
}
