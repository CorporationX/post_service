package faang.school.postservice.util.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exceptions.PostAlreadyPublishedException;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.publisher.PostViewEventPublisher;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.service.PostService;
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
import java.util.stream.Stream;

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
    private LikeRepository likeRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private AdRepository adRepository;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private AlbumRepository albumRepository;
    @InjectMocks
    private PostService postService;
    @Captor
    private ArgumentCaptor<Post> postCaptor;
    @Mock
    private PostViewEventPublisher postViewEventPublisher;

    @Test
    public void testPositivePublish() {
        Post post = Post.builder()
                .id(1L)
                .published(false)
                .build();
        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        PostDto postDto = postService.publish(post.getId());
        verify(postRepository, times(1)).save(post);

        assertEquals(post.getId(), postDto.id());
        assertTrue(postDto.published());
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
        PostDto postDto1 = postService.update(postDto, 1L);
        verify(postRepository, times(1)).save(post);
        assertEquals(postDto1.content(), postDto.content());
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
        PostDto dto = postService.getPost(post.getId(),1L);
        assertEquals(post.getId(), dto.id());
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
        Stream<Post> stream = Stream.of(post, post1, post2);

        when(postRepository.findByAuthorId(1L)).thenReturn(stream);
        List<PostDto> list = postService.findDraftsByAuthorId(1L,1L);

        assertEquals(1, list.size());
        assertEquals(post.getId(), list.get(0).id());
        assertEquals(post.isDeleted(), list.get(0).deleted());
        assertEquals(post.isPublished(), list.get(0).published());
        assertEquals(post.getContent(), list.get(0).content());
    }

    @Test
    public void testNegativeFindDraftsByAuthorIdIsEmpty() {
        Stream<Post> stream = Stream.empty();

        when(postRepository.findByAuthorId(1L)).thenReturn(stream);
        List<PostDto> list = postService.findDraftsByAuthorId(1L,1L);

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
        Stream<Post> stream = Stream.of(post, post1, post2);

        when(postRepository.findByProjectId(1L)).thenReturn(stream);
        List<PostDto> list = postService.findDraftsByProjectId(1L,1L);

        assertEquals(1, list.size());
        assertEquals(post.getId(), list.get(0).id());
        assertEquals(post.isDeleted(), list.get(0).deleted());
        assertEquals(post.isPublished(), list.get(0).published());
        assertEquals(post.getContent(), list.get(0).content());
    }

    @Test
    public void testNegativeFindDraftsByProjectIdIsEmpty() {
        Stream<Post> stream = Stream.empty();

        when(postRepository.findByProjectId(1L)).thenReturn(stream);
        List<PostDto> list = postService.findDraftsByProjectId(1L,1L);

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
        Stream<Post> stream = Stream.of(post, post1, post2);

        when(postRepository.findByAuthorId(1L)).thenReturn(stream);
        List<PostDto> list = postService.findPublishedByAuthorId(1L,1L);

        assertEquals(1, list.size());
        assertEquals(post.getId(), list.get(0).id());
        assertEquals(post.isDeleted(), list.get(0).deleted());
        assertEquals(post.isPublished(), list.get(0).published());
        assertEquals(post.getContent(), list.get(0).content());
    }

    @Test
    public void testNegativeFindPublishedByAuthorIdIsEmpty() {
        Stream<Post> stream = Stream.empty();

        when(postRepository.findByAuthorId(1L)).thenReturn(stream);
        List<PostDto> list = postService.findPublishedByAuthorId(1L,1L);

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
        Stream<Post> stream = Stream.of(post, post1, post2);

        when(postRepository.findByProjectId(1L)).thenReturn(stream);
        List<PostDto> list = postService.findPublishedByProjectId(1L,1L);

        assertEquals(1, list.size());
        assertEquals(post.getId(), list.get(0).id());
        assertEquals(post.isDeleted(), list.get(0).deleted());
        assertEquals(post.isPublished(), list.get(0).published());
        assertEquals(post.getContent(), list.get(0).content());
    }

    @Test
    public void testNegativeFindPublishedByProjectIdIsEmpty() {
        Stream<Post> stream = Stream.empty();

        when(postRepository.findByProjectId(1L)).thenReturn(stream);
        List<PostDto> list = postService.findPublishedByProjectId(1L,1L);

        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
    }

    @Test
    public void testPositiveCreate() {
        PostDto postDto = PostDto.builder()
                .authorId(1L)
                .content("content")
                .build();
        when(adRepository.findById(any())).thenReturn(Optional.of(Ad.builder().build()));
        when(commentRepository.findByIdIn(any())).thenReturn(List.of());
        when(likeRepository.findByIdIn(any())).thenReturn(List.of());
        when(albumRepository.findByIdIn(any())).thenReturn(List.of());
        when(resourceRepository.findByIdIn(any())).thenReturn(List.of());

        PostDto postDto1 = postService.create(postDto);

        verify(postRepository, times(1)).save(postCaptor.capture());
        Post post = postCaptor.getValue();

        assertEquals("content", post.getContent());
        assertEquals(0,post.getLikes().size());
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
}
