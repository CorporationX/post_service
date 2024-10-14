package faang.school.postservice.service;

import faang.school.postservice.cache.entity.PostCache;
import faang.school.postservice.cache.entity.UserCache;
import faang.school.postservice.cache.repository.PostCacheRepository;
import faang.school.postservice.cache.repository.UserCacheRepository;
import faang.school.postservice.client.HashtagServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.hashtag.HashtagRequest;
import faang.school.postservice.dto.hashtag.HashtagResponse;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostResponse;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.event.kafka.KafkaPostEvent;
import faang.school.postservice.event.kafka.KafkaPostViewEvent;
import faang.school.postservice.mapper.PostContextMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaPostEventProducer;
import faang.school.postservice.producer.KafkaPostViewEventProducer;
import faang.school.postservice.redisPublisher.PostEventPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.elasticsearchService.ElasticsearchService;
import faang.school.postservice.validator.PostServiceValidator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostCacheRepository postCacheRepository;

    @Mock
    private SpellCheckerService spellCheckerService;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PostMapper postMapper;

    @Mock
    private PostServiceValidator postServiceValidator;

    @Mock
    private HashtagServiceClient hashtagServiceClient;

    @Mock
    private ElasticsearchService elasticsearchService;

    @Mock
    private EntityManager entityManager;

    @Mock
    private PostContextMapper postContextMapper;
    private List<Post> postList;

    @Mock
    private PostEventPublisher postEventPublisher;

    @Mock
    private UserContext userContext;
    @Mock
    private KafkaPostEventProducer kafkaPostEventProducer;
    @Mock
    private UserCacheRepository userCacheRepository;
    @Mock
    private KafkaPostViewEventProducer kafkaPostViewEventProducer;

    @Captor
    private ArgumentCaptor<PostCache> postCacheArgumentCaptor;
    private Long userId;
    private UserDto userDto;
    private PostDto postDto;
    private Post post;
    private List<Post> draftPosts;
    private List<Post> publishedPosts;
    private List<PostDto> draftPostDtos;
    private List<PostDto> publishedPostDtos;
    private List<String> hashtagNames;
    private HashtagRequest hashtagRequest;
    private List<Hashtag> hashtags;

    @BeforeEach
    public void setUp() {
        long firstPostId = 1L;
        long secondPostId = 2L;
        userId = 3L;
        userDto = UserDto.builder()
                .id(userId)
                .build();
        String firstPostContent = "FirstPostContent";
        String secondPostContent = "SecondPostContent";

        postList = List.of(
                Post.builder()
                        .id(firstPostId)
                        .content(firstPostContent).build(),
                Post.builder()
                        .id(secondPostId)
                        .content(secondPostContent).build()
        );

        postDto = new PostDto();
        post = new Post();
        Post draftPost1 = Post.builder()
                .id(1L)
                .content("Draft 1")
                .published(true)
                .publishedAt(LocalDateTime.now())
                .build();

        Post draftPost2 = Post.builder()
                .id(2L)
                .content("Draft 2")
                .published(true)
                .publishedAt(LocalDateTime.now())
                .build();

        Post publishedPost1 = Post.builder().id(3L)
                .content("Published 1")
                .published(true)
                .publishedAt(LocalDateTime.now())
                .build();

        Post publishedPost2 = Post.builder().id(2L)
                .content("Published 2")
                .published(true)
                .publishedAt(LocalDateTime.now())
                .build();

        draftPosts = Arrays.asList(draftPost1, draftPost2);
        publishedPosts = Arrays.asList(publishedPost1, publishedPost2);

        PostDto draftPostDto1 = PostDto.builder()
                .id(1L)
                .content("Draft 1")
                .build();

        PostDto draftPostDto2 = PostDto.builder()
                .id(2L)
                .content("Draft 2")
                .build();

        PostDto publishedPostDto1 = PostDto.builder()
                .id(3L)
                .content("Published 1")
                .hashtagNames(List.of(""))
                .build();

        PostDto publishedPostDto2 = PostDto.builder()
                .id(4L)
                .content("Published 2")
                .hashtagNames(List.of(""))
                .build();

        hashtagNames = new ArrayList<>();
        hashtagNames.add("#hashtag1");
        hashtagNames.add("#hashtag2");
        hashtagNames.add("#hashtag3");

        hashtagRequest = HashtagRequest.builder()
                .hashtagNames(hashtagNames)
                .build();
        Hashtag hashtag = Hashtag.builder()
                .id(1)
                .name("#hashtag1")
                .posts(draftPosts)
                .build();
        hashtags = new ArrayList<>();
        hashtags.add(hashtag);
        hashtags.add(hashtag);
        hashtags.add(hashtag);

        postDto = PostDto.builder()
                .id(1L)
                .authorId(1L)
                .projectId(1L)
                .content("New post")
                .hashtagNames(hashtagNames)
                .build();

        post = Post.builder()
                .id(1L)
                .authorId(1L)
                .projectId(null)
                .content("New post")
                .hashtags(hashtags)
                .build();

        draftPostDtos = Arrays.asList(draftPostDto1, draftPostDto2);
        publishedPostDtos = Arrays.asList(publishedPostDto1, publishedPostDto2);
    }

    @Test
    @DisplayName("testing correctPostsContent method")
    void testCorrectPostsContent() {
        postService.correctPostsContent(postList);
        verify(spellCheckerService, times(2)).checkMessage(anyString());
        verify(postRepository, times(1)).saveAll(postList);
    }

    @Test
    @DisplayName("Test creating a new post")
    public void testCreatePost() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);

        doNothing().when(postServiceValidator).validateCreatePost(postDto);
        doNothing().when(hashtagServiceClient).saveHashtags(hashtagRequest);
        when(hashtagServiceClient.getHashtagsByNames(hashtagRequest)).thenReturn(new HashtagResponse(hashtags));
        when(entityManager.merge(any(Hashtag.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.toDto(any(Post.class))).thenReturn(postDto);
        postDto.setHashtagNames(hashtagNames);

        PostDto result = postService.createPost(postDto);

        verify(postServiceValidator, times(1)).validateCreatePost(postDto);
        verify(hashtagServiceClient, times(1)).saveHashtags(hashtagRequest);
        verify(postRepository, times(1)).save(any(Post.class));
        verify(elasticsearchService, times(1)).indexPost(postDto);
        assertEquals(postDto, result);
        verify(postEventPublisher, times(1)).publish(any());
        verify(postCacheRepository, times(1)).save(postCacheArgumentCaptor.capture());
        verify(userCacheRepository, times(1)).save(any(UserCache.class));
        verify(kafkaPostEventProducer, times(1)).sendMessage(any(KafkaPostEvent.class));
    }

    @Test
    @DisplayName("Test updating an existing post")
    public void testUpdatePost() {
        when(postRepository.findById(postDto.getId())).thenReturn(Optional.of(post));
        doNothing().when(postServiceValidator).validateUpdatePost(post, postDto);
        doNothing().when(hashtagServiceClient).saveHashtags(any(HashtagRequest.class));
        when(hashtagServiceClient.getHashtagsByNames(any(HashtagRequest.class))).thenReturn(new HashtagResponse(hashtags));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.toDto(any(Post.class))).thenReturn(postDto);

        PostDto result = postService.updatePost(postDto);

        verify(postRepository, times(1)).findById(postDto.getId());
        verify(postServiceValidator, times(1)).validateUpdatePost(post, postDto);
        verify(hashtagServiceClient, times(1)).saveHashtags(any(HashtagRequest.class));
        verify(postRepository, times(1)).save(any(Post.class));
        verify(elasticsearchService, times(1)).indexPost(postDto);
        assertEquals(postDto, result);
    }

    @Test
    @DisplayName("Test publishing a post")
    public void testPublishPost() {
        when(postRepository.findById(postDto.getId())).thenReturn(Optional.of(post));
        doNothing().when(postServiceValidator).validatePublishPost(post);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        when(postMapper.toDto(any(Post.class))).thenReturn(postDto);

        PostDto result = postService.publishPost(postDto);

        verify(postRepository, times(1)).findById(postDto.getId());
        verify(postServiceValidator, times(1)).validatePublishPost(post);
        verify(postRepository, times(1)).save(post);
        verify(postMapper, times(1)).toDto(post);

        assertTrue(post.isPublished());
        assertNotNull(post.getPublishedAt());
        assertEquals(postDto, result);
    }

    @Test
    @DisplayName("Test deleting a post when the post is found")
    public void testDeletePostPostFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        doNothing().when(postServiceValidator).validateDeletePost(post);

        PostDto result = postService.deletePost(1L);

        verify(postRepository, times(1)).save(post);
        verify(elasticsearchService, times(1)).removePost(1L);
        assertTrue(post.isDeleted());
        assertFalse(post.isPublished());
        assertEquals(postMapper.toDto(post), result);
    }

    @Test
    @DisplayName("Test finding posts by hashtag in cache")
    public void testFindPostsByHashtag() {
        String hashtagName = "#hashtag1";
        List<PostDto> postDtos = List.of(postDto);
        when(hashtagServiceClient.findPostsByHashtag(hashtagName)).thenReturn(new PostResponse(postDtos));

        List<PostDto> result = postService.findPostsByHashtag(hashtagName, 0, 1000);

        assertEquals(postDtos, result);
        verify(hashtagServiceClient, times(1)).findPostsByHashtag(hashtagName);
        verify(elasticsearchService, times(0)).searchPostsByHashtag(hashtagName, 0, 1000);
    }

    @Test
    @DisplayName("Test finding posts by hashtag when no posts found in cache")
    public void testFindPostsByHashtagNoPostsFound() {
        String hashtagName = "#hashtag1";
        List<PostDto> emptyPostDtos = Collections.emptyList();
        when(hashtagServiceClient.findPostsByHashtag(hashtagName)).thenReturn(new PostResponse(emptyPostDtos));

        List<PostDto> result = postService.findPostsByHashtag(hashtagName, 0, 1000);

        assertEquals(emptyPostDtos, result);
        verify(hashtagServiceClient, times(1)).findPostsByHashtag(hashtagName);
        verify(elasticsearchService, times(1)).searchPostsByHashtag(hashtagName, 0, 1000);
    }

    @Test
    @DisplayName("Test deleting a post when the post is not found")
    public void testDeletePostPostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.deletePost(1L));
    }

    @Test
    @DisplayName("Test getting a post by its ID when the post is not found")
    public void testGetPostByPostIdPostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.getPostDtoById(1L));
    }

    @Test
    @DisplayName("Test getting a post by its ID when the post is found")
    public void testGetPostByPostIdPostFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.getPostDtoById(1L);

        verify(kafkaPostViewEventProducer, times(1)).sendMessage(any(KafkaPostViewEvent.class));
        verify(postMapper, times(1)).toDto(post);
    }

    @Test
    @DisplayName("Test getting all draft posts by user ID")
    void getAllDraftPostsByUserId() {
        when(postRepository.findByAuthorId(1L)).thenReturn(draftPosts);
        when(postMapper.toDto(anyList())).thenReturn(draftPostDtos);

        List<PostDto> result = postService.getAllDraftPostsByUserId(1L);

        assertEquals(2, result.size());
        assertEquals(draftPostDtos, result);
    }

    @Test
    @DisplayName("Test getting all draft posts by project ID")
    void getAllDraftPostsByProjectId() {
        when(postRepository.findByProjectId(1L)).thenReturn(draftPosts);
        when(postMapper.toDto(anyList())).thenReturn(draftPostDtos);

        List<PostDto> result = postService.getAllDraftPostsByProjectId(1L);

        assertEquals(2, result.size());
        assertEquals(draftPostDtos, result);
    }

    @Test
    @DisplayName("Test getting all published posts by user ID")
    void getAllPublishPostsByUserId() {
        when(postRepository.findByAuthorId(1L)).thenReturn(publishedPosts);
        when(postMapper.toDto(anyList())).thenReturn(publishedPostDtos);

        List<PostDto> result = postService.getAllPublishPostsByUserId(1L);

        assertEquals(2, result.size());
        assertEquals(publishedPostDtos, result);
    }

    @Test
    @DisplayName("Test getting all published posts by project ID")
    void getAllPublishPostsByProjectId() {
        when(postRepository.findByProjectId(1L)).thenReturn(publishedPosts);
        when(postMapper.toDto(anyList())).thenReturn(publishedPostDtos);

        List<PostDto> result = postService.getAllPublishPostsByProjectId(1L);

        assertEquals(2, result.size());
        assertEquals(publishedPostDtos, result);
    }

    @Test
    public void testGetPostWhenNotDataBase() {
        long postId = 1;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> postService.getPost(postId));
        verify(postContextMapper, never()).getCountLikeEveryonePost();
    }

    @Test
    public void testGetPostWhenValid() {
        long postId = 1;
        Post post = new Post();
        post.setId(postId);
        post.setLikes(Arrays.asList(new Like(), new Like()));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post result = postService.getPost(postId);

        assertDoesNotThrow(() -> postService.getPost(postId));
        assertEquals(post, result);
    }
}
