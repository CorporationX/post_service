package faang.school.postservice.service;

import faang.school.postservice.client.HashtagServiceClient;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.component.RedisRepositoryCoordinator;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.PostResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.PostAlreadyPublishedException;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.VerifiedStatus;
import faang.school.postservice.publisher.HashtagAddingEventPublisher;
import faang.school.postservice.publisher.HashtagRemovingEventPublisher;
import faang.school.postservice.publisher.PostEventPublisher;
import faang.school.postservice.publisher.PostViewEventPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Spy
    private PostMapperImpl postMapper;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private AdRepository adRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private PostService postService;

    @Captor
    private ArgumentCaptor<Post> postCaptor;

    @Mock
    private PostViewEventPublisher postViewEventPublisher;

    @Mock
    private HashtagAddingEventPublisher hashtagAddingPublisher;

    @Mock
    private HashtagRemovingEventPublisher hashtagRemovingPublisher;

    @Mock
    private HashtagServiceClient hashtagClient;

    @Mock
    private PostViewEventPublisher viewEventPublisher;

    @Mock
    private PostEventPublisher postEventPublisher;

    @Mock
    private RedisRepositoryCoordinator redisRepositoryCoordinator;

    @Test
    public void testPositivePublish() {
        Post post = Post.builder()
                .id(1L)
                .authorId(1L)
                .verifiedStatus(VerifiedStatus.APPROVED)
                .published(false)
                .build();
        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        PostResponseDto postDto = postService.publish(post.getId());

        verify(postRepository, times(1)).save(post);

        assertEquals(post.getId(), postDto.getId());
        assertTrue(postDto.isPublished());
    }

    @Test
    public void testNegativePublishPostIdIsNull() {
        assertThrows(RuntimeException.class, () -> postService.publish(null));
    }

    @Test
    public void testNegativePublishIsNotPublished() {
        Post post = Post.builder()
                .id(1L)
                .published(true)
                .build();
        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        assertThrows(PostAlreadyPublishedException.class, () -> postService.publish(post.getId()));
    }

    @Test
    public void update() {
        Post post = Post.builder()
                .id(1L)
                .published(true)
                .build();
        PostDto postDto = PostDto.builder()
                .content("content")
                .build();
        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        PostResponseDto postDto1 = postService.update(postDto, 1L);
        verify(postRepository, times(1)).save(post);
        assertEquals(postDto1.getContent(), postDto.content());
    }

    @Test
    public void testNegativeUpdatePostDtoIsNull() {
        assertThrows(NullPointerException.class, () -> postService.update(null, 1L));
    }

    @Test
    public void testNegativeUpdateContentIsBlank() {
        assertThrows(NullPointerException.class, () -> postService.update(PostDto.builder()
                .content("")
                .build(), 1L));
    }

    @Test
    public void testNegativeUpdateContentIsNull() {
        assertThrows(NullPointerException.class, () -> postService.update(PostDto.builder()
                .content(null)
                .build(), 1L));
    }

    @Test
    public void testPositiveDelete() {
        Post post = Post.builder()
                .id(1L)
                .published(true)
                .deleted(false)
                .build();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        postService.deleteById(post.getId());
        verify(postRepository, times(1)).save(post);
        assertTrue(post.isDeleted());
        assertFalse(post.isPublished());
    }

    @Test
    public void testPositiveGetPost() {
        Post post = Post.builder()
                .id(1L)
                .build();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        PostResponseDto dto = postService.getPost(post.getId(),1L);
        assertEquals(post.getId(), dto.getId());
    }

    @Test
    public void testPositiveFindDraftsByAuthorId() {
        Post post = Post.builder()
                .id(1L)
                .deleted(false)
                .published(false)
                .content("content")
                .build();
        Post post1 = Post.builder()
                .id(1L)
                .published(true)
                .deleted(false)
                .build();
        Post post2 = Post.builder()
                .id(2L)
                .published(true)
                .deleted(true)
                .build();
        List<Post> posts = List.of(post, post1, post2);

        when(postRepository.findByAuthorId(1L)).thenReturn(posts);
        List<PostResponseDto> list = postService.findDraftsByAuthorId(1L,1L);

        assertEquals(1, list.size());
        assertEquals(post.getId(), list.get(0).getId().intValue());
        assertEquals(post.isPublished(), list.get(0).isPublished());
        assertEquals(post.getContent(), list.get(0).getContent());
    }

    @Test
    public void testNegativeFindDraftsByAuthorIdIsEmpty() {
        List<Post> posts = Collections.emptyList();

        when(postRepository.findByAuthorId(1L)).thenReturn(posts);
        List<PostResponseDto> list = postService.findDraftsByAuthorId(1L,1L);

        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
    }

    @Test
    public void testPositiveFindDraftsByProjectId() {
        Post post = Post.builder()
                .id(1L)
                .deleted(false)
                .published(false)
                .content("content")
                .build();
        Post post1 = Post.builder()
                .id(1L)
                .published(true)
                .deleted(false)
                .build();
        Post post2 = Post.builder()
                .id(2L)
                .published(true)
                .deleted(true)
                .build();
        List<Post> posts = List.of(post, post1, post2);

        when(postRepository.findByProjectId(1L)).thenReturn(posts);
        List<PostResponseDto> list = postService.findDraftsByProjectId(1L,1L);

        assertEquals(1, list.size());
        assertEquals(post.getId(), list.get(0).getId().intValue());
        assertEquals(post.isPublished(), list.get(0).isPublished());
        assertEquals(post.getContent(), list.get(0).getContent());
    }

    @Test
    public void testNegativeFindDraftsByProjectIdIsEmpty() {
        List<Post> posts = Collections.emptyList();

        when(postRepository.findByProjectId(1L)).thenReturn(posts);
        List<PostResponseDto> list = postService.findDraftsByProjectId(1L,1L);

        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
    }

    @Test
    public void testPositiveFindPublishedByAuthorId() {
        Post post = Post.builder()
                .id(1L)
                .deleted(false)
                .published(true)
                .content("content")
                .build();
        Post post1 = Post.builder()
                .id(1L)
                .published(false)
                .deleted(false)
                .build();
        Post post2 = Post.builder()
                .id(2L)
                .published(true)
                .deleted(true)
                .build();
        List<Post> posts = List.of(post, post1, post2);

        when(postRepository.findByAuthorId(1L)).thenReturn(posts);
        List<PostResponseDto> list = postService.findPublishedByAuthorId(1L,1L);

        assertEquals(1, list.size());
        assertEquals(post.getId(), list.get(0).getId().intValue());
        assertEquals(post.isPublished(), list.get(0).isPublished());
        assertEquals(post.getContent(), list.get(0).getContent());
    }

    @Test
    public void testNegativeFindPublishedByAuthorIdIsEmpty() {
        List<Post> posts = Collections.emptyList();

        when(postRepository.findByAuthorId(1L)).thenReturn(posts);
        List<PostResponseDto> list = postService.findPublishedByAuthorId(1L,1L);

        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
    }

    @Test
    public void testPositiveFindPublishedByProjectId() {
        Post post = Post.builder()
                .id(1L)
                .deleted(false)
                .published(true)
                .content("content")
                .build();
        Post post1 = Post.builder()
                .id(1L)
                .published(false)
                .deleted(false)
                .build();
        Post post2 = Post.builder()
                .id(2L)
                .published(true)
                .deleted(true)
                .build();
        List<Post> posts = List.of(post, post1, post2);

        when(postRepository.findByProjectId(1L)).thenReturn(posts);
        List<PostResponseDto> list = postService.findPublishedByProjectId(1L,1L);

        assertEquals(1, list.size());
        assertEquals(post.getId(), list.get(0).getId().intValue());
        assertEquals(post.isPublished(), list.get(0).isPublished());
        assertEquals(post.getContent(), list.get(0).getContent());
    }

    @Test
    public void testNegativeFindPublishedByProjectIdIsEmpty() {
        List<Post> posts = Collections.emptyList();

        when(postRepository.findByProjectId(1L)).thenReturn(posts);
        List<PostResponseDto> list = postService.findPublishedByProjectId(1L,1L);

        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
    }

    @Test
    public void testPositiveCreate() {
        PostDto postDto = PostDto.builder()
                .authorId(1L)
                .content("content")
                .build();
        when(resourceRepository.findByIdIn(any())).thenReturn(List.of());

        postService.create(postDto);

        verify(postRepository, times(1)).save(postCaptor.capture());
        Post post = postCaptor.getValue();

        assertEquals("content", post.getContent());
        assertEquals(0, post.getLikes().size());
    }

    @Test
    public void testNegativeCreatePostDtoIsNull() {
        assertThrows(NullPointerException.class, () -> postService.create(null));
    }

    @Test
    public void testNegativeCreateContentIsNull() {
        assertThrows(NullPointerException.class, () -> postService.create(PostDto.builder().build()));
    }

    @Test
    public void testNegativeCreateContentIsEmpty() {
        assertThrows(NullPointerException.class, () -> postService.create(PostDto.builder()
                .content("")
                .build()));
    }

    @Test
    public void testPositiveGetPostsByIds() {
        String content = "content";
        List<Long> postIds = List.of(1L, 2L, 3L);
        List<Post> posts = List.of(
                createPost(postIds.get(0), content),
                createPost(postIds.get(1), content),
                createPost(postIds.get(2), content)
        );
        List<PostResponseDto> responsePosts = List.of(
                createPostDto(postIds.get(0), content),
                createPostDto(postIds.get(1), content),
                createPostDto(postIds.get(2), content)
        );
        when(postRepository.findAllByIdIn(postIds)).thenReturn(posts);

        List<PostResponseDto> result = postService.getPostsByIds(postIds);

        assertEquals(3, result.size());
        assertEquals(responsePosts.get(0), result.get(0));
    }

    @Test
    public void testNegativeCreateValidateAuthor() {
        assertThrows(IllegalArgumentException.class, () -> postService.create(PostDto.builder()
                .projectId(1L)
                .authorId(1L)
                .content("content")
                .build()));
    }

    private Post createPost(Long id, String content) {
        return Post.builder()
                .id(id)
                .content(content)
                .build();
    }

    private PostResponseDto createPostDto(Long id, String content) {
        return PostResponseDto.builder()
                .id(id)
                .content(content)
                .likeCount(0)
                .commentsId(Collections.emptyList())
                .albumsId(Collections.emptyList())
                .resourcesId(Collections.emptyList())
                .build();
    }

    private UserDto createUserDto(Long id) {
        return UserDto.builder()
                .id(id)
                .username("randomUsername")
                .build();
    }
}
