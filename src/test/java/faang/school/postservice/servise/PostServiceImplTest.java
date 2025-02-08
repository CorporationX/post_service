package faang.school.postservice.servise;

import faang.school.postservice.dto.posts.PostDto;
import faang.school.postservice.dto.posts.PostSaveDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.PostRepositoryAdapter;
import faang.school.postservice.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {
    @InjectMocks
    private PostServiceImpl postService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostRepositoryAdapter postRepositoryAdapter;
    @Mock
    private PostMapper postMapper;

    private static final long USER_ID = 1L;
    private static final long ID = 1L;

    private Post post;
    private Post savedPost;
    private PostDto postDto;

    @BeforeEach
    void setUp() {
        post = new Post();
        post.setId(ID);
        post.setAuthorId(USER_ID);

        savedPost = new Post();

        postDto = new PostDto();
        postDto.setId(ID);
    }

    @Test
    void create() {
        PostSaveDto postSaveDto = new PostSaveDto();
        postSaveDto.setAuthorId(USER_ID);
        postSaveDto.setContent("Test Content");

        when(postMapper.toEntity(postSaveDto)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(savedPost);
        when(postMapper.toDto(savedPost)).thenReturn(postDto);

        PostDto result = postService.create(postSaveDto);

        assertNotNull(result);
        assertEquals(postDto.getId(), result.getId());
        verify(postMapper, times(1)).toEntity(postSaveDto);
        verify(postRepository, times(1)).save(post);
        verify(postMapper, times(1)).toDto(savedPost);
    }

    @Test
    void getPost() {
        when(postRepositoryAdapter.findById(ID)).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(postDto);

        PostDto result = postService.getPost(ID);

        assertNotNull(result);
        assertEquals(postDto.getId(), result.getId());
        verify(postRepositoryAdapter, times(1)).findById(ID);
        verify(postMapper, times(1)).toDto(post);
    }

    @Test
    void update() {
        PostSaveDto postSaveDto = new PostSaveDto();
        postSaveDto.setAuthorId(USER_ID);
        postSaveDto.setContent("Updated Content");

        when(postRepositoryAdapter.findById(ID)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(savedPost);
        when(postMapper.toDto(savedPost)).thenReturn(postDto);

        PostDto result = postService.update(ID, postSaveDto);

        assertNotNull(result);
        assertEquals(postDto.getId(), result.getId());
        verify(postRepositoryAdapter, times(1)).findById(ID);
        verify(postRepository, times(1)).save(post);
        verify(postMapper, times(1)).toDto(savedPost);
    }

    @Test
    void publish() {
        when(postRepositoryAdapter.findById(ID)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(savedPost);

        postService.publish(ID);

        assertTrue(post.isPublished());
        assertNotNull(post.getPublishedAt());
        verify(postRepositoryAdapter, times(1)).findById(ID);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void delete() {
        when(postRepositoryAdapter.findById(ID)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(savedPost);

        postService.delete(ID);

        assertTrue(post.isDeleted());
        verify(postRepositoryAdapter, times(1)).findById(ID);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void getPostsByAuthorId() {
        List<Post> posts = Collections.singletonList(post);
        when(postRepository.findByAuthorId(eq(2L), anyBoolean())).thenReturn(posts);
        when(postMapper.toDto(posts)).thenReturn(Collections.singletonList(postDto));

        List<PostDto> result = postService.getPostsByAuthorId(2L, true);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postRepository, times(1)).findByAuthorId(2L, true);
        verify(postMapper, times(1)).toDto(posts);
    }

    @Test
    void getPostsByProjectId() {
        List<Post> posts = Collections.singletonList(post);
        when(postRepository.findByProjectId(eq(3L), anyBoolean())).thenReturn(posts);
        when(postMapper.toDto(posts)).thenReturn(Collections.singletonList(postDto));

        List<PostDto> result = postService.getPostsByProjectId(3L, true);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postRepository, times(1)).findByProjectId(3L, true);
        verify(postMapper, times(1)).toDto(posts);
    }

    @Test
    void publishingPostsOnSchedule() {
        when(postRepository.publishingPostsOnSchedule()).thenReturn(5);

        int result = postService.publishingPostsOnSchedule();

        assertEquals(5, result);
        verify(postRepository, times(1)).publishingPostsOnSchedule();
    }
}
