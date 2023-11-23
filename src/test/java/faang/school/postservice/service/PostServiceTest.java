package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.KafkaPostView;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.RedisPostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.messaging.KafkaPostViewProducer;
import faang.school.postservice.messaging.publishing.NewPostPublisher;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.publisher.PostViewEventPublisher;
import faang.school.postservice.repository.PostRedisRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private AdRepository adRepository;
    @Mock
    private PostRepository postRepository;
    @Spy
    private PostMapperImpl postMapper;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PostViewEventPublisher postViewEventPublisher;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @Mock
    private PostRedisRepository postRedisRepository;
    @Mock
    private NewPostPublisher newPostPublisher;
    @Mock
    private KafkaPostViewProducer kafkaPostViewProducer;
    @InjectMocks
    private PostService postService;
    private Post postOne;
    private Post postTwo;
    private Post postTree;

    private PostDto postDtoOne;
    private PostDto postDtoTwo;
    private PostDto postDtoTree;
    List<PostDto> posts;

    private UpdatePostDto updatePostDto;


    @BeforeEach
    void setUp() {
        posts = new ArrayList<>();
        postOne = Post.builder().id(1L)
                .createdAt(LocalDateTime.of(2022, 3, 1, 0, 0))
                .deleted(false).published(true).build();
        postTwo = Post.builder().id(2L)
                .createdAt(LocalDateTime.of(2022, 1, 1, 0, 0))
                .deleted(false).published(false).build();
        postTree = Post.builder().id(3L)
                .createdAt(LocalDateTime.of(2022, 2, 1, 0, 0))
                .deleted(false).published(true).build();
        postDtoOne = PostDto.builder().id(1L).createdAt(LocalDateTime.of(2022, 3, 1, 0, 0))
                .deleted(false).published(true).build();
        postDtoTwo = PostDto.builder().id(2L).createdAt(LocalDateTime.of(2022, 1, 1, 0, 0))
                .deleted(false).published(false).build();
        postDtoTree = PostDto.builder().id(3L).createdAt(LocalDateTime.of(2022, 2, 1, 0, 0))
                .deleted(false).published(true).build();
        updatePostDto = UpdatePostDto.builder().adId(1L).build();

        posts.add(postDtoOne);
        posts.add(postDtoTree);

    }

    @Test
    void testCreatePostDataValidationException() {
        CreatePostDto createPostDto = CreatePostDto.builder().authorId(1L).projectId(1L).build();
        assertThrows(DataValidationException.class, () -> postService.createPost(createPostDto));
    }

    @Test
    void testCreatePostMockAuthorDataValidationException() {
        CreatePostDto createPostDto = CreatePostDto.builder().authorId(1L).projectId(null).build();

        when(userServiceClient.getUserInternal(1L)).thenReturn(null);
        assertThrows(DataValidationException.class, () -> postService.createPost(createPostDto));
    }

    @Test
    void testCreatePostMockProjectDataValidationException() {
        CreatePostDto createPostDto = CreatePostDto.builder().authorId(null).projectId(1L).build();

        when(projectServiceClient.getProject(1L)).thenReturn(null);
        assertThrows(DataValidationException.class, () -> postService.createPost(createPostDto));
    }

    @Test
    void testCreatePost() {
        Post post = Post.builder()
                .authorId(null).projectId(1L)
                .deleted(false).published(false).build();
        CreatePostDto createPostDto = CreatePostDto.builder().authorId(null).projectId(1L).build();
        RedisPostDto redisPostDto = RedisPostDto.builder().id(1L).content("test").authorId(1L).build();

        when(projectServiceClient.getProject(1L)).thenReturn(new ProjectDto());
        postService.createPost(createPostDto);
        postRedisRepository.save(redisPostDto);
        verify(postRepository).save(post);
    }

    @Test
    void testPublishPostArrayList() {
        when(postRepository.findReadyToPublish()).thenReturn(new ArrayList<>());
        assertEquals(new ArrayList<>(), postService.publishPost());
    }

    @Test
    void testPublishPostSave() {
        Post postTwo2 = Post.builder().id(2L)
                .createdAt(LocalDateTime.of(2022, 1, 1, 0, 0))
                .deleted(false).published(true).build();

        when(postRepository.findReadyToPublish()).thenReturn(new ArrayList<>(List.of(postOne, postTwo, postTree)));
        postService.publishPost();
        verify(postRepository).save(postTwo2);
    }

    @Test
    void testPublishPost() {
        Post postTwo2 = Post.builder().id(2L)
                .createdAt(LocalDateTime.of(2022, 1, 1, 0, 0))
                .deleted(false).published(true).build();

        when(postRepository.findReadyToPublish()).thenReturn(new ArrayList<>(List.of(postOne, postTwo, postTree)));
        postService.publishPost();
        verify(postMapper).toDtoList(List.of(postOne, postTwo2, postTree));
    }

    @Test
    void testUpdatePostDataValidationException() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () -> postService.updatePost(1L, updatePostDto));
    }

    @Test
    void testUpdatePostAdDataValidationException() {
        Post post123 = Post.builder().id(1L).ad(Ad.builder().id(1L).build()).build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post123));
        when(adRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> postService.updatePost(1L, updatePostDto));
    }

    @Test
    void testUpdatePost() {
        Post post = Post.builder().id(1L).build();
        PostDto postDto = PostDto.builder().id(1L).build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(adRepository.findById(1L)).thenReturn(Optional.of(Ad.builder().id(1L).build()));
        when(postMapper.toDto(post)).thenReturn(postDto);
        when(postRepository.save(post)).thenReturn(post);

        assertEquals(postDto, postService.updatePost(1L, updatePostDto));
    }

    @Test
    void softDeletePostDataValidationException() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () -> postService.softDeletePost(1L));
    }

    @Test
    void softDeletePost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(postOne));
        when(postMapper.toDto(postOne)).thenReturn(postDtoOne);
        when(postRepository.save(postOne)).thenReturn(postOne);

        assertEquals(postDtoOne, postService.softDeletePost(1L));
    }

    @Test
    void getPostByIdDataValidationException() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () -> postService.getPostById(1L));
    }

    @Test
    void getPostById() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(postOne));
        when(postMapper.toDto(postOne)).thenReturn(postDtoOne);
        doNothing().when(kafkaPostViewProducer).sendMessage(Mockito.any(KafkaPostView.class));
        assertEquals(postDtoOne, postService.getPostById(1L));
    }

    @Test
    void getAllPostsByAuthorId() {
        when(postRepository.findByAuthorId(1L)).thenReturn(List.of(postOne, postTwo, postTree));
        when(postMapper.toDto(postTwo)).thenReturn(postDtoTwo);

        assertEquals(List.of(postDtoTwo), postService.getAllPostsByAuthorId(1L));
    }

    @Test
    void getAllPostsByProjectId() {
        when(postRepository.findByProjectId(1L)).thenReturn(List.of(postOne, postTwo, postTree));
        when(postMapper.toDto(postTwo)).thenReturn(postDtoTwo);

        assertEquals(List.of(postDtoTwo), postService.getAllPostsByProjectId(1L));
    }

    @Test
    void getAllPostsByAuthorIdAndPublished() {
        when(postRepository.findByAuthorId(1L)).thenReturn(List.of(postOne, postTwo, postTree));
        when(postMapper.toDto(postOne)).thenReturn(postDtoOne);
        when(postMapper.toDto(postTree)).thenReturn(postDtoTree);

        assertEquals(posts, postService.getAllPostsByAuthorIdAndPublished(1L));
    }

    @Test
    void getAllPostsByProjectIdAndPublished() {
        when(postRepository.findByProjectId(1L)).thenReturn(List.of(postOne, postTwo, postTree));
        when(postMapper.toDto(postOne)).thenReturn(postDtoOne);
        when(postMapper.toDto(postTree)).thenReturn(postDtoTree);

        assertEquals(posts, postService.getAllPostsByProjectIdAndPublished(1L));
    }
}