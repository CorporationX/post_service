package faang.school.postservice.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @InjectMocks
    private PostService postService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostMapper postMapper;
    @Mock
    private PostValidator postValidator;

    private PostDto postDto1;
    private PostDto postDto2;
    private Post post1;
    private Post post2;
    private Post post3;
    private Post post4;
    private Post post5;
    private Long postId;
    private String content;

    @BeforeEach
    void setUp() {
        postId = 10L;
        content = "text";

        postDto1 = new PostDto();
        postDto1.setId(1L);
        postDto1.setAuthorId(1L);
        postDto1.setProjectId(1L);
        postDto1.setContent("content");
        postDto1.setCreatedAt(LocalDateTime.now().minusDays(1));

        postDto2 = new PostDto();
        postDto2.setId(2L);
        postDto2.setAuthorId(2L);
        postDto2.setProjectId(2L);
        postDto2.setContent("Content");
        postDto2.setCreatedAt(LocalDateTime.now());

        post1 = new Post();
        post1.setId(1L);
        post1.setAuthorId(1L);
        post1.setProjectId(1L);
        post1.setContent("content");
        post1.setPublished(false);
        post1.setDeleted(false);

        post2 = new Post();
        post2.setId(2L);
        post2.setAuthorId(2L);
        post2.setProjectId(2L);
        post2.setContent("Content");
        post2.setPublished(false);
        post2.setDeleted(false);

        post3 = new Post();
        post3.setId(3L);
        post3.setPublished(true);
        post3.setDeleted(false);
        post3.setCreatedAt(LocalDateTime.now());

        post4 = new Post();
        post4.setId(2L);
        post4.setPublished(true);
        post4.setDeleted(false);
        post4.setCreatedAt(LocalDateTime.now().minusDays(1));

        post5 = new Post();
        post5.setId(5L);
        post5.setPublished(false);
        post5.setDeleted(true);
    }

    @Test
    @DisplayName("Creating post")
    public void testCreatePost() {
        when(postMapper.toEntity(any(PostDto.class))).thenReturn(post1);
        when(postMapper.toDto(any(Post.class))).thenReturn(postDto1);
        when(postRepository.save(any(Post.class))).thenReturn(post1);

        PostDto createdPost = postService.create(postDto1);

        verify(postValidator).validateAuthorIdAndProjectId(postDto1.getAuthorId(), postDto1.getProjectId());
        verify(postRepository).save(post1);
        verify(postMapper).toEntity(postDto1);
        verify(postMapper).toDto(post1);

        assertNotNull(createdPost);
        assertEquals(postDto1.getId(), createdPost.getId());
        assertEquals(postDto1.getAuthorId(), createdPost.getAuthorId());
        assertEquals(postDto1.getProjectId(), createdPost.getProjectId());
        assertEquals(postDto1.getContent(), createdPost.getContent());
    }

    @Test
    @DisplayName("Publishing post")
    public void testPublishPost() {
        doNothing().when(postValidator).validatePublicationPost(post1);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post1));
        when(postMapper.toDto(any(Post.class))).thenReturn(postDto1);
        when(postRepository.save(any(Post.class))).thenReturn(post1);

        PostDto publishPost = postService.publish(1L);

        verify(postRepository).findById(1L);
        verify(postValidator).validatePublicationPost(post1);
        assertTrue(post1.isPublished());
        assertNotNull(post1.getPublishedAt());
        verify(postRepository).save(post1);
        verify(postMapper).toDto(post1);

        assertNotNull(publishPost);
        assertEquals(postDto1.getId(), publishPost.getId());
    }

    @Test
    @DisplayName("Updating post")
    public void testUpdatePost() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post1));
        when(postMapper.toDto(any(Post.class))).thenReturn(postDto1);
        when(postRepository.save(any(Post.class))).thenReturn(post1);

        PostDto updatedPost = postService.update(postId, content);

        verify(postRepository).findById(postId);
        assertNotNull(post1.getContent());
        verify(postRepository).save(post1);
        verify(postMapper).toDto(post1);

        assertNotNull(updatedPost);
        assertEquals(postDto1.getId(), post1.getId());
    }

    @Test
    @DisplayName("Deleting post")
    public void testDeleteByIdPost() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post1));
        when(postRepository.save(any(Post.class))).thenReturn(post1);

        postService.deleteById(postId);

        verify(postRepository).findById(postId);
        assertTrue(post1.isDeleted());
        verify(postRepository).save(post1);
    }

    @Test
    @DisplayName("Search for a post by ID")
    public void testFindByIdPost() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post1));

        Post foundPost = postService.findById(postId);

        verify(postRepository).findById(postId);

        assertNotNull(foundPost);
        assertEquals(1L, foundPost.getId());
    }

    @Test
    @DisplayName("Getting all drafts of non-deleted posts authored by user ID")
    public void testGetAllPostsDraftsByUserAuthorId() {
        when(postRepository.findByAuthorId(1L)).thenReturn(Arrays.asList(post1, post2));
        when(postMapper.toDto(post1)).thenReturn(postDto1);
        when(postMapper.toDto(post2)).thenReturn(postDto2);

        List<PostDto> postsDrafts = postService.getAllPostsDraftsByUserAuthorId(1L);

        verify(postRepository).findByAuthorId(1L);
        verify(postMapper).toDto(post1);
        verify(postMapper).toDto(post2);

        assertNotNull(postsDrafts);
        assertEquals(2, postsDrafts.size());
        assertEquals(postDto2.getId(), postsDrafts.get(0).getId());
        assertEquals(postDto1.getId(), postsDrafts.get(1).getId());
    }

    @Test
    @DisplayName("Getting all drafts of non-deleted posts authored by project ID")
    public void testGetAllPostsDraftsByProjectAuthorId() {
        when(postRepository.findByProjectId(1L)).thenReturn(Arrays.asList(post1, post2));
        when(postMapper.toDto(post1)).thenReturn(postDto1);
        when(postMapper.toDto(post2)).thenReturn(postDto2);

        List<PostDto> postsDrafts = postService.getAllPostsDraftsByProjectAuthorId(1L);

        verify(postRepository).findByProjectId(1L);
        verify(postMapper).toDto(post1);
        verify(postMapper).toDto(post2);

        assertNotNull(postsDrafts);
        assertEquals(2, postsDrafts.size());
        assertEquals(postDto2.getId(), postsDrafts.get(0).getId());
        assertEquals(postDto1.getId(), postsDrafts.get(1).getId());
    }

    @Test
    @DisplayName("Getting all published, non-deleted posts authored by user id")
    public void testGetAllPublishedNonDeletedPostsByUserAuthorId() {
        when(postRepository.findByAuthorId(1L)).thenReturn(Arrays.asList(post3, post4, post5));
        when(postMapper.toDto(post3)).thenReturn(postDto1);
        when(postMapper.toDto(post4)).thenReturn(postDto2);

        List<PostDto> publishedPosts = postService.getAllPublishedNonDeletedPostsByUserAuthorId(1L);

        verify(postRepository).findByAuthorId(1L);
        verify(postMapper).toDto(post3);
        verify(postMapper).toDto(post4);
        verify(postMapper, never()).toDto(post5);

        assertNotNull(publishedPosts);
        assertEquals(2, publishedPosts.size());
        assertEquals(postDto2.getId(), publishedPosts.get(0).getId());
        assertEquals(postDto1.getId(), publishedPosts.get(1).getId());
    }

    @Test
    @DisplayName("Getting all published, non-deleted posts authored by project id")
    public void testGetAllPublishedNonDeletedPostsByProjectAuthorId() {
        when(postRepository.findByProjectId(1L)).thenReturn(Arrays.asList(post3, post4, post5));
        when(postMapper.toDto(post3)).thenReturn(postDto1);
        when(postMapper.toDto(post4)).thenReturn(postDto2);

        List<PostDto> publishedPosts = postService.getAllPublishedNonDeletedPostsByProjectAuthorId(1L);

        verify(postRepository).findByProjectId(1L);
        verify(postMapper).toDto(post3);
        verify(postMapper).toDto(post4);
        verify(postMapper, never()).toDto(post5);

        assertNotNull(publishedPosts);
        assertEquals(2, publishedPosts.size());
        assertEquals(postDto2.getId(), publishedPosts.get(0).getId());
        assertEquals(postDto1.getId(), publishedPosts.get(1).getId());
    }
}