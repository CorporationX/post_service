package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    private Post post;
    private PostDto postDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        post = new Post();
        post.setId(1L);
        post.setContent("Test post");

        postDto = new PostDto();
        postDto.setId(1L);
        postDto.setContent("Test post DTO");
    }

    @Test
    void testGetAllPostsByHashtagId() {
        Pageable pageable = mock(Pageable.class);
        Page<Post> postPage = new PageImpl<>(List.of(post));

        when(postRepository.findByHashtagsContent(anyString(), any(Pageable.class))).thenReturn(postPage);
        when(postMapper.toPostDto(any(Post.class))).thenReturn(postDto);

        Page<PostDto> result = postService.getAllPostsByHashtagId("#hashtag", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(postRepository, times(1)).findByHashtagsContent(anyString(), any(Pageable.class));
        verify(postMapper, times(1)).toPostDto(any(Post.class));
    }

    @Test
    void testGetPostByIdInternal_PostExists() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Post result = postService.getPostByIdInternal(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPostByIdInternal_PostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            postService.getPostByIdInternal(1L);
        });

        assertEquals("'Post not in database' error occurred while fetching post", exception.getMessage());
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdatePostInternal() {
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post result = postService.updatePostInternal(post);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void testGetUserPublishedPosts() {
        Long authorId = 1L;

        post.setPublished(true);
        post.setDeleted(false);

        when(postRepository.findByAuthorIdWithLikes(authorId)).thenReturn(List.of(post));
        when(postMapper.toPostDto(any(Post.class))).thenReturn(postDto);

        List<PostDto> result = postService.getUserPublishedPosts(authorId);

        assertEquals(1, result.size());
        assertEquals(postDto.getId(), result.get(0).getId());
        assertEquals(postDto.getPublishedAt(), result.get(0).getPublishedAt());
        verify(postRepository, times(1)).findByAuthorIdWithLikes(authorId);
        verify(postMapper, times(1)).toPostDto(any(Post.class));
    }

    @Test
    void testGetProjectPublishedPosts() {
        Long projectId = 2L;

        post.setPublished(true);
        post.setDeleted(false);

        when(postRepository.findByProjectIdWithLikes(projectId)).thenReturn(List.of(post));
        when(postMapper.toPostDto(any(Post.class))).thenReturn(postDto);

        List<PostDto> result = postService.getProjectPublishedPosts(projectId);

        assertEquals(1, result.size());
        assertEquals(postDto.getId(), result.get(0).getId());
        assertEquals(postDto.getPublishedAt(), result.get(0).getPublishedAt());
        verify(postRepository, times(1)).findByProjectIdWithLikes(projectId);
        verify(postMapper, times(1)).toPostDto(any(Post.class));
    }
}