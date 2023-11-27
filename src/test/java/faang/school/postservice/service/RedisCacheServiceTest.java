package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.mapper.redis.RedisCommentMapper;
import faang.school.postservice.mapper.redis.RedisCommentMapperImpl;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.mapper.redis.RedisPostMapperImpl;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.mapper.redis.RedisUserMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.model.redis.RedisUser;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisKeyValueTemplate;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisCacheServiceTest {

    @Mock
    private RedisUserRepository redisUserRepository;
    @Mock
    private RedisPostRepository redisPostRepository;
    @Spy
    private RedisUserMapper redisUserMapper = new RedisUserMapperImpl();
    @Spy
    private RedisCommentMapper redisCommentMapper = new RedisCommentMapperImpl();
    @Spy
    private RedisPostMapper redisPostMapper = new RedisPostMapperImpl(redisCommentMapper);
    @Mock
    private RedisKeyValueTemplate redisKeyValueTemplate;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private RedisCacheService redisCacheService;

    private UserDto userDto;
    private UserDto updatedUserDto;

    private RedisUser redisUser;
    private RedisUser expectedUser;

    private Post post;
    private Post updatedPost;

    private RedisPost redisPost;
    private RedisPost expectedPost;

    private final Long userId = 1L;
    private final Long postId = 1L;
    private final Long postAuthorId = 1L;
    private final String postContent = "Post Content";
    private final String updatedPostContent = "Updated Post Content";
    private final String username = "Yevhenii";
    private final String email = "email@gmail.com";
    private final String updatedEmail = "updatedEmail@gmail.com";
    private final String phone = "+788005553535";
    private final String updatedPhone = "+753535550088";

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(userId)
                .username(username)
                .email(email)
                .phone(phone)
                .build();
        updatedUserDto = UserDto.builder()
                .id(userId)
                .username(username)
                .email(updatedEmail)
                .phone(updatedPhone)
                .build();
        redisUser = RedisUser.builder()
                .userId(userId)
                .username(username)
                .email(email)
                .phone(phone)
                .version(1)
                .build();
        post = Post.builder()
                .id(postId)
                .content(postContent)
                .authorId(postAuthorId)
                .published(true)
                .deleted(false)
                .build();
        updatedPost = Post.builder()
                .id(postId)
                .content(updatedPostContent)
                .authorId(postAuthorId)
                .published(true)
                .deleted(false)
                .build();
        redisPost = RedisPost.builder()
                .postId(postId)
                .content(postContent)
                .authorId(postAuthorId)
                .version(1)
                .build();
        expectedPost = RedisPost.builder()
                .postId(postId)
                .content(postContent)
                .authorId(postAuthorId)
                .commentsDto(Collections.emptyList())
                .postViews(0L)
                .postLikes(0L)
                .version(1)
                .build();
        expectedUser = RedisUser.builder()
                .userId(userId)
                .username(username)
                .email(updatedEmail)
                .phone(updatedPhone)
                .version(2)
                .build();
    }

    @Test
    void findAndCacheRedisUserTest() {
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(redisUserRepository.save(redisUser)).thenReturn(redisUser);

        RedisUser result = redisCacheService.findOrCacheRedisUser(userId);

        assertEquals(redisUser, result);
        assertEquals(1, result.getVersion());

        verify(redisUserRepository).findById(userId);
        verify(userServiceClient).getUser(userId);
        verify(redisUserRepository).save(redisUser);
    }

    @Test
    void updateOrCacheFirstScenarioTest() {
        when(redisUserRepository.findById(userId)).thenReturn(Optional.of(redisUser));
        when(redisKeyValueTemplate.update(userId, expectedUser)).thenReturn(expectedUser);

        RedisUser result = redisCacheService.updateOrCacheUser(updatedUserDto);

        assertEquals(expectedUser, result);

        verify(redisUserRepository).findById(userId);
        verify(redisKeyValueTemplate).update(userId, expectedUser);
    }

    @Test
    void updateOrCacheSecondScenarioTest() {
        when(redisUserRepository.findById(userId)).thenReturn(Optional.empty());
        when(redisUserRepository.save(redisUser)).thenReturn(redisUser);

        redisUser.setEmail(updatedEmail);
        redisUser.setPhone(updatedPhone);

        RedisUser result = redisCacheService.updateOrCacheUser(updatedUserDto);

        assertEquals(redisUser, result);
        verify(redisUserRepository).save(redisUser);
    }

    @Test
    void updateOrCachePostFirstScenarioTest() {
        when(redisPostRepository.findById(postId)).thenReturn(Optional.of(expectedPost));
        when(redisKeyValueTemplate.update(postId, expectedPost)).thenReturn(expectedPost);

        expectedPost.setContent(updatedPostContent);
        expectedPost.setVersion(2);

        RedisPost result = redisCacheService.updateOrCachePost(updatedPost);

        assertEquals(expectedPost, result);

        verify(redisPostRepository).findById(postId);
        verify(redisKeyValueTemplate).update(postId, expectedPost);
    }

    @Test
    void updateOrCachePostSecondScenarioTest() {
        when(redisPostRepository.findById(postId)).thenReturn(Optional.empty());
        when(redisPostRepository.save(expectedPost)).thenReturn(expectedPost);

        expectedPost.setContent(updatedPostContent);

        RedisPost result = redisCacheService.updateOrCachePost(updatedPost);

        assertEquals(expectedPost, result);

        verify(redisPostRepository).findById(postId);
        verify(redisPostRepository).save(expectedPost);
    }

    @Test
    void updateUserTest() {
        when(redisKeyValueTemplate.update(userId, expectedUser)).thenReturn(expectedUser);

        RedisUser result = redisCacheService.updateUser(redisUser, updatedUserDto);

        assertEquals(expectedUser, result);
        verify(redisKeyValueTemplate).update(userId, expectedUser);
    }

    @Test
    void updatePostTest() {
        when(redisKeyValueTemplate.update(postId, expectedPost)).thenReturn(expectedPost);

        expectedPost.setContent(updatedPostContent);
        expectedPost.setVersion(2);

        RedisPost result = redisCacheService.updatePost(redisPost, updatedPost);

        assertEquals(expectedPost, result);
        verify(redisKeyValueTemplate).update(postId, expectedPost);
    }

    @Test
    void cacheUserTest() {
        when(redisUserRepository.save(redisUser)).thenReturn(redisUser);
        RedisUser result = redisCacheService.cacheUser(userDto);

        assertEquals(redisUser, result);
        verify(redisUserRepository).save(redisUser);
    }

    @Test
    void cachePostTest() {
        when(redisPostRepository.save(expectedPost)).thenReturn(expectedPost);
        RedisPost result = redisCacheService.cachePost(post);

        assertEquals(expectedPost, result);
        verify(redisPostRepository).save(expectedPost);
    }

    @Test
    void mapUserToRedisUserAndSetDefaultVersionTest() {
        RedisUser result = redisCacheService.mapUserToRedisUserAndSetDefaultVersion(userDto);
        assertEquals(redisUser, result);
    }

    @Test
    void mapPostToRedisPostAndSetDefaultVersionTest() {
        RedisPost result = redisCacheService.mapPostToRedisPostAndSetDefaultVersion(post);
        assertEquals(expectedPost, result);
    }
}