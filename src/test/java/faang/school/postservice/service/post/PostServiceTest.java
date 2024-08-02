package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaPostProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @InjectMocks
    private PostServiceImpl postService;

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
    private LocalDateTime publicationTime;

    @BeforeEach
    void setUp() {
        postId = 10L;
        content = "text";
        publicationTime = LocalDateTime.now();

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
    public void shouldCreatePost() {
        when(postMapper.toEntity(any(PostDto.class))).thenReturn(post1);
        when(postMapper.toDto(any(Post.class))).thenReturn(postDto1);
        when(postRepository.save(any(Post.class))).thenReturn(post1);
        PostDto createdPost = postService.createPost(postDto1);
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
    public void shouldPublishPost() {
        doNothing().when(postValidator).validatePublicationPost(post1);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post1));
        when(postMapper.toDto(any(Post.class))).thenReturn(postDto1);
        PostDto publishPost = postService.publishPost(1L);
        verify(postRepository).findById(1L);
        verify(postValidator).validatePublicationPost(post1);
        assertTrue(post1.isPublished());
        verify(postMapper).toDto(post1);
        assertNotNull(publishPost);
        assertEquals(postDto1.getId(), publishPost.getId());
    }

    @Test
    public void shouldUpdatePost() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post1));
        when(postMapper.toDto(any(Post.class))).thenReturn(postDto1);
        PostDto updatedPost = postService.updatePost(postId, content, publicationTime);
        verify(postRepository).findById(postId);
        assertNotNull(post1.getContent());
        verify(postMapper).toDto(post1);
        assertNotNull(updatedPost);
        assertEquals(postDto1.getId(), post1.getId());
    }

    @Test
    public void shouldDeleteByIdPost() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post1));
        postService.deletePostById(postId);
        verify(postRepository).findById(postId);
        assertTrue(post1.isDeleted());
    }

    @Test
    public void shouldFindByIdPost() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post1));
        Post foundPost = postService.findById(postId);
        verify(postRepository).findById(postId);
        assertNotNull(foundPost);
        assertEquals(1L, foundPost.getId());
    }

    @Test
    public void shouldGetAllPostsDraftsByUserAuthorId() {
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
    public void shouldGetAllPostsDraftsByProjectAuthorId() {
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
    public void shouldGetAllPublishedNotDeletedPostsByUserAuthorId() {
        when(postRepository.findByAuthorId(1L)).thenReturn(Arrays.asList(post3, post4, post5));
        when(postMapper.toDto(post3)).thenReturn(postDto1);
        when(postMapper.toDto(post4)).thenReturn(postDto2);
        List<PostDto> publishedPosts = postService.getAllPublishedNotDeletedPostsByUserAuthorId(1L);
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
    public void shouldGetAllPublishedNotDeletedPostsByProjectAuthorId() {
        when(postRepository.findByProjectId(1L)).thenReturn(Arrays.asList(post3, post4, post5));
        when(postMapper.toDto(post3)).thenReturn(postDto1);
        when(postMapper.toDto(post4)).thenReturn(postDto2);
        List<PostDto> publishedPosts = postService.getAllPublishedNotDeletedPostsByProjectAuthorId(1L);
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