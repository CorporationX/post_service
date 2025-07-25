package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.redis.RedisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Spy
    private PostMapperImpl postMapper;
    @Captor
    ArgumentCaptor<Post> postCaptor;
    @Mock
    private RedisService redisService;
    @InjectMocks
    private PostService postService;

    @Test
    void getPostDtoById() {
        Long postId = 1L;

        Post post = createPost(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(redisService.getPost(anyString())).thenReturn(null);

        PostResponseDto result = postService.getPostDtoById(postId);

        assertNotNull(result);
        assertEquals(postId, result.getId());
    }

    @Test
    void getUserByIdTestException() {
        long id = -1L;
        when(postRepository.findById(id))
                .thenThrow(new IllegalArgumentException("There is no such id = " + id));

        assertThrows(IllegalArgumentException.class, () -> postRepository.findById(id));
    }


    @Test
    public void testGetPostResponseDtoById() {
        Post post = createPost(1L);
        long postId = post.getId();

        when(postRepository.findById(postId))
                .thenReturn(Optional.of(post));
        when(redisService.getPost(anyString())).thenReturn(null);

        PostResponseDto result = postService.getPostDtoById(postId);

        assertNotNull(result);
        assertEquals(postMapper.toDto(post), result);
    }

    @Test
    public void testGetPostResponseDtoByIdNotFound() {
        long postId = 1L;

        when(postRepository.findById(postId))
                .thenThrow(new IllegalArgumentException("There is no such id = " + postId));

        assertThrows(IllegalArgumentException.class, () -> postRepository.findById(postId));
    }

    @Test
    public void testCreateDraftPost() {
        PostRequestDto request = new PostRequestDto("Draft", 1L, null);

        postService.createDraftPost(request);

        verify(postRepository, times(1)).save(postCaptor.capture());
        Post savedPost = postCaptor.getValue();
        assertNotNull(savedPost);
    }

    @Test
    public void testPublishPost() {
        Post post = createPost(1L);
        long postId = post.getId();

        when(postRepository.findById(postId))
                .thenReturn(Optional.of(post));

        postService.publishPost(postId);

        verify(postRepository).save(postCaptor.capture());
        Post publishedPost = postCaptor.getValue();

        assertTrue(publishedPost.isPublished());
        assertNotNull(publishedPost.getPublishedAt());
    }

    @Test
    public void testPublishAlreadyPublishedPost() {
        Post post = createPost(1L);
        long postId = post.getId();
        post.setPublished(true);

        when(postRepository.findById(postId))
                .thenReturn(Optional.of(post));

        assertThrows(IllegalArgumentException.class, () -> postService.publishPost(postId));
    }

    @Test
    public void testUpdatePost() {
        Post post = createPost(1L);
        long postId = post.getId();
        post.setContent("Draft");

        when(postRepository.findById(postId))
                .thenReturn(Optional.of(post));

        postService.updatePost(postId,
                new PostRequestDto("Post", 1L, null));

        verify(postRepository, times(1)).save(postCaptor.capture());
        Post updatedPost = postCaptor.getValue();

        assertNotNull(updatedPost);
        assertNotEquals("Draft", updatedPost.getContent());
    }

    @Test
    public void testDeletePost() {
        Post post = createPost(1L);
        long postId = post.getId();

        when(postRepository.findById(postId))
                .thenReturn(Optional.of(post));

        postService.deletePost(postId);

        assertTrue(post.isDeleted());
    }

    @Test
    public void testGetAllNotDeletedDraftsByAuthorId() {
        Post draft = createPost(1L);
        draft.setAuthorId(2L);
        long draftAuthorId = draft.getAuthorId();

        when(postRepository.findByAuthorId(draftAuthorId))
                .thenReturn(List.of(draft));

        List<PostResponseDto> result = postService.getAllNotDeletedDraftsByAuthorId(draftAuthorId);

        assertEquals(draft.getId(), result.get(0).getId());
    }

    @Test
    public void testGetAllNotDeletedDraftsByProjectId() {
        Post draft = createPost(1L);
        draft.setProjectId(2L);
        long draftProjectId = draft.getProjectId();

        when(postRepository.findByProjectId(draftProjectId))
                .thenReturn(List.of(draft));

        List<PostResponseDto> result = postService.getAllNotDeletedDraftsByProjectId(draftProjectId);

        assertEquals(draft.getId(), result.get(0).getId());
    }

    @Test
    public void testGetAllPostsByAuthorId() {
        Post post = createPost(1L);
        post.setAuthorId(2L);
        post.setDeleted(false);
        post.setPublished(true);
        long postAuthorId = post.getAuthorId();

        when(postRepository.findByAuthorId(postAuthorId))
                .thenReturn(List.of(post));

        List<PostResponseDto> result = postService.getAllPostsByAuthorId(postAuthorId);

        assertEquals(post.getId(), result.get(0).getId());
    }

    @Test
    public void testGetAllPostsByProjectId() {
        Post post = createPost(2L);
        post.setProjectId(2L);
        post.setPublished(true);
        long postProjectId = post.getProjectId();

        when(postRepository.findByProjectId(postProjectId))
                .thenReturn(List.of(post));

        List<PostResponseDto> result = postService.getAllPostsByProjectId(postProjectId);

        assertEquals(post.getId(), result.get(0).getId());
    }

    private Post createPost(long id) {
        return Post.builder()
                .id(id)
                .build();
    }
}