package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    @InjectMocks
    private PostService postService;

    @Test
    void getPostById() {
        Post post = createPost(1L);
        long postId = post.getId();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post result = postService.getPostById(postId);

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
    public void testGetPostDtoById() {
        Post post = createPost(1L);
        PostDto dto = postMapper.toDto(post);
        long postId = post.getId();

        when(postRepository.findById(postId))
                .thenReturn(Optional.of(post));

        PostDto result = postService.getPostDtoById(postId);

        assertNotNull(result);
        assertEquals(dto, result);
    }


    @Test
    public void testGetPostDtoByIdNotFound() {
        long postId = 1L;

        when(postRepository.findById(postId))
                .thenThrow(new IllegalArgumentException("There is no such id = " + postId));

        assertThrows(IllegalArgumentException.class, () -> postRepository.findById(postId));
    }

    @Test
    public void testCreateDraftPost() {
        PostDto postDto = new PostDto
                (1L, "Post", 2L, null,
                        false, null,
                        null, null, false);

        postService.createDraftPost(postDto);

        verify(postRepository, times(1)).save(postCaptor.capture());
        Post savedPost = postCaptor.getValue();
        assertEquals(1L, savedPost.getId());
        assertFalse(savedPost.isDeleted());
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

        assertThrows(IllegalStateException.class, () -> postService.publishPost(postId));
    }

    @Test
    public void testUpdatePost() {
        Post post = createPost(1L);
        long postId = post.getId();
        post.setContent("Draft");

        when(postRepository.findById(postId))
                .thenReturn(Optional.of(post));

        postService.updatePost(postId,
                new PostDto(1L, "Post",
                        null, null, false, null,
                        null, null, false));

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

        List<PostDto> result = postService.getAllNotDeletedDraftsByAuthorId(draftAuthorId);

        assertEquals(List.of(draft), result.stream()
                .map(postMapper::toEntity)
                .toList());
    }

    @Test
    public void testGetAllNotDeletedDraftsByProjectId() {
        Post draft = createPost(1L);
        draft.setProjectId(2L);
        long draftProjectId = draft.getProjectId();

        when(postRepository.findByProjectId(draftProjectId))
                .thenReturn(List.of(draft));

        List<PostDto> result = postService.getAllNotDeletedDraftsByProjectId(draftProjectId);

        assertEquals(List.of(draft), result.stream()
                .map(postMapper::toEntity)
                .toList());
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

        List<PostDto> result = postService.getAllPostsByAuthorId(postAuthorId);

        assertEquals(List.of(post), result.stream()
                .map(postMapper::toEntity)
                .toList());
    }

    @Test
    public void testGetAllPostsByProjectId() {
        Post post = createPost(2L);
        post.setProjectId(2L);
        post.setPublished(true);
        long postProjectId = post.getProjectId();

        when(postRepository.findByProjectId(postProjectId))
                .thenReturn(List.of(post));

        List<PostDto> result = postService.getAllPostsByProjectId(postProjectId);

        assertEquals(List.of(post), result.stream()
                .map(postMapper::toEntity)
                .toList());
    }

    private Post createPost(long id) {
        return Post.builder().id(id).build();
    }
}