package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.dto.postCorrector.AiResponseDto;
import faang.school.postservice.dto.postCorrector.ResponseFieldDto;
import faang.school.postservice.dto.redis.LikeEventDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.post.ResponsePostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.ModerationDictionary;
import faang.school.postservice.util.RedisPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Spy
    private ResponsePostMapper responsePostMapper = ResponsePostMapper.INSTANCE;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @Mock
    private ModerationDictionary moderationDictionary;
    @Mock
    private RedisPublisher redisPublisher;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private LikeEventPublisher likeEventPublisher;
    private final Integer batchSize = 100;
    private final String userBannerChannel = "user_banner_channel";
    private PostService postService;
    @Mock
    private RestTemplate restTemplate;
    private final String postCorrectorApiKey = "http://some-key";
    private final String postCorrectorUrl = "https://api.textgears.com/correct?text=";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        postService = new PostService(postRepository, responsePostMapper,
                userServiceClient, projectServiceClient, moderationDictionary,
                batchSize, redisPublisher, likeRepository, likeEventPublisher,
                userBannerChannel, restTemplate,
                postCorrectorApiKey, postCorrectorUrl);
    }

    @Test
    void publishTest() {
        Post post = Post.builder().id(1L).published(false).deleted(false).build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        ResponsePostDto result = postService.publish(1L);

        assertTrue(result.isPublished());
    }

    @Test
    void updateTest() {
        Post post = Post.builder().id(1L).content("Before").build();
        UpdatePostDto dto = new UpdatePostDto(1L, "After");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        ResponsePostDto result = postService.update(dto);

        assertEquals("After", post.getContent());
    }

    @Test
    void softDeleteTest() {
        Post post = Post.builder().id(1L).deleted(false).build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        ResponsePostDto result = postService.softDelete(1L);

        assertTrue(result.isDeleted());
    }

    @Test
    void createTest() {
        CreatePostDto correct = CreatePostDto.builder().authorId(1L).content("Content").build();
        UserDto userDto = new UserDto(1L, "username", "email@com");
        LocalDateTime now = LocalDateTime.now();
        Post post = Post.builder().authorId(1L).content("Content").createdAt(now).published(false).deleted(false).build();

        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        ResponsePostDto result = postService.createDraft(correct);

        assertEquals(1, result.getAuthorId());
        assertEquals("Content", result.getContent());
        assertEquals(now, result.getCreatedAt());
        assertFalse(result.isPublished());
        assertFalse(result.isDeleted());
    }

    @Test
    void createThrowsExceptions() {
        CreatePostDto bothNotNull = CreatePostDto.builder().authorId(1L).projectId(1L).build();
        CreatePostDto blankContent = CreatePostDto.builder().authorId(1L).content("").build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> postService.createDraft(bothNotNull));
        assertThrows(NullPointerException.class, () -> postService.createDraft(blankContent));
        assertEquals("Both AuthorId and ProjectId can't be not null", exception.getMessage());
    }

    @Test
    void verifyContent_Test() {
        List<Post> posts = new ArrayList<>();

        for (int i = 0; i < batchSize * 2; i++) {
            Post post = Post.builder()
                    .id(i)
                    .verified(false)
                    .verifiedAt(null)
                    .build();
            if (post.getId() % 2 == 0) {
                post.setContent("Something arse something");
            } else {
                post.setContent("To verify");
            }
            posts.add(post);
        }

        when(postRepository.findAllByVerifiedAtIsNull()).thenReturn(posts);
        when(moderationDictionary.containsBadWord(anyString())).thenReturn(false);

        postService.verifyContent();

        verify(postRepository).findAllByVerifiedAtIsNull();
        verify(moderationDictionary, times(posts.size())).containsBadWord(anyString());
        verify(postRepository, times(posts.size() / batchSize)).saveAll(anyList());
    }

    @Test
    void banForOffensiveContentTest() {
        Post post = Post.builder().authorId(1L).build();
        List<Post> posts = new ArrayList<>(List.of(post, post, post, post, post, post));
        UserDto userDto = UserDto.builder().id(1L).banned(false).build();
        List<UserDto> users = new ArrayList<>(List.of(userDto));

        when(postRepository.findAllByVerifiedFalseAndVerifiedAtIsNotNull()).thenReturn(posts);
        when(userServiceClient.getUsersByIds(anyList())).thenReturn(users);

        postService.banForOffensiveContent();

        verify(redisPublisher).publishMessage(userBannerChannel, "1");
    }

    @Test
    void testOrderByDate() {
        String hashtag = "test";
        when(postRepository.findByHashtagOrderByDate("#" + hashtag)).thenReturn(createPostList());

        List<ResponsePostDto> result = postService.getPostsByHashtagOrderByDate(hashtag);

        assertNotNull(result);
        assertEquals(createPostDtoList().get(0).getId(), result.get(0).getId());
    }

    @Test
    void testOrderByPopularity() {
        String hashtag = "test";
        when(postRepository.findByHashtagOrderByPopularity("#" + hashtag)).thenReturn(createPostList());

        List<ResponsePostDto> result = postService.getPostsByHashtagOrderByPopularity(hashtag);

        assertNotNull(result);
        assertEquals(createPostDtoList().get(0).getId(), result.get(0).getId());
    }

    @Disabled
    @Test
    void correctPostsTest() {
        Post post = Post.builder().content("Wrong").build();
        AiResponseDto response = AiResponseDto.builder().response(new ResponseFieldDto("Correct")).build();
        List<Post> posts = new ArrayList<>(List.of(post, post, post, post, post));
        String url = "https://api.textgears.com/correct?text=" + post.getContent() + "&language=en-GB&key=" + postCorrectorApiKey;

        when(postRepository.findAllByPublishedFalseAndDeletedFalse()).thenReturn(posts);
        when(restTemplate.exchange(url, HttpMethod.GET, null, AiResponseDto.class))
                .thenReturn(ResponseEntity.ok(response));

        postService.correctPosts();

        for (Post result : posts) {
            assertEquals("Correct", result.getContent());
        }
    }

    private List<Post> createPostList() {
        List<Hashtag> hashtags = createHashtagList();
        return List.of(Post.builder().id(1).hashtags(hashtags).build(),
                Post.builder().id(2).hashtags(hashtags).build(),
                Post.builder().id(3).hashtags(hashtags).build(),
                Post.builder().id(4).hashtags(hashtags).build(),
                Post.builder().id(5).hashtags(hashtags).build());
    }

    private List<ResponsePostDto> createPostDtoList() {
        List<String> hashtags = List.of("#test");
        return List.of(ResponsePostDto.builder().id(1).hashtags(hashtags).build(),
                ResponsePostDto.builder().id(2).hashtags(hashtags).build(),
                ResponsePostDto.builder().id(3).hashtags(hashtags).build(),
                ResponsePostDto.builder().id(4).hashtags(hashtags).build(),
                ResponsePostDto.builder().id(5).hashtags(hashtags).build());
    }

    private List<Hashtag> createHashtagList() {
        return List.of(Hashtag.builder().id(1L).hashtag("#test").build());
    }

    @Test
    void likePostTest() {
        Long postId = 1L;
        Long userId = 2L;
        Post post = new Post();
        post.setId(postId);
        post.setAuthorId(userId);
        post.setLikes(List.of(Like.builder().id(12).userId(13L).build()));

        UpdatePostDto updatePostDto = new UpdatePostDto(postId, "qweqwe");

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ResponsePostDto responsePostDto = postService.likePost(updatePostDto, userId);

        verify(likeRepository, times(1)).save(any());
        verify(likeEventPublisher, times(1)).publishMessage(LikeEventDto.builder().postId(1L).postAuthor(2L).likeAuthor(2L).dateTime(any()).build());

        assertNotNull(responsePostDto);
    }
}