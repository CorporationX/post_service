package faang.school.postservice.util.service.like.impl;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.like.impl.PostLikeServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.like.kafka.KafkaLikePublisher;
import faang.school.postservice.service.like.redis.RedisLikeCache;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты CommentLikeServiceImpl")
public class PostLikeServiceImplTest {
    @Mock
    private LikeRepository likeRepository;

    @Mock
    private RedisLikeCache redisLikeCache;

    @Mock
    @Qualifier("kafkaConsumer")
    private KafkaLikePublisher kafkaLikePublisher;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostLikeServiceImpl postLikeService;

    @Test
    @DisplayName("Тест добавления лайка к посту - успешный сценарий")
    public void addLike_Success() {

        UserDto userDto = UserDto.builder()
                .id(2L)
                .username("testUser")
                .build();

        Post post = Post.builder()
                .id(1L)
                .content("This is a test post")
                .build();

        Long postId = 1L;
        Long userId = 2L;

        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(postRepository.existsById(postId)).thenReturn(true);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());

        doNothing().when(redisLikeCache).incrementLikes("postLikes:", postId);

        postLikeService.addLike(postId, userId);

        verify(userServiceClient).getUser(userId);
        verify(postRepository).existsById(postId);
        verify(likeRepository).findByPostIdAndUserId(postId, userId);
        verify(redisLikeCache).incrementLikes("postLikes:", postId);
    }

    @Test
    @DisplayName("Тест добавления лайка к посту - ошибка, если лайк уже существует")
    public void addLike_AlreadyExists() {
        Long postId = 1L;
        Long userId = 2L;

        when(likeRepository.findByPostIdAndUserId(postId, userId))
                .thenReturn(Optional.of(Like.builder().id(1L).build()));

        assertThatThrownBy(() -> postLikeService.addLike(postId, userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Лайк под этим постом уже оставлен пользователем с id: "
                        + userId + " id поста: " + postId);

        verify(likeRepository).findByPostIdAndUserId(postId, userId);
        verify(postRepository, never()).findById(postId);
        verify(redisLikeCache, never()).incrementLikes("postLikes:", postId);

    }


    @Test
    @DisplayName("Тест удаления лайка из поста - успешный сценарий")
    public void removeLike_Success() {
        UserDto userDto = UserDto.builder()
                .id(2L)
                .username("testUser")
                .build();

        Post post = Post.builder()
                .id(1L)
                .content("This is a test post")
                .build();

        Long postId = 1L;
        Long userId = 2L;

        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(postRepository.existsById(postId)).thenReturn(true);
        when(likeRepository.findByPostIdAndUserId(postId, userId))
                .thenReturn(Optional.of(Like.builder().id(1L).build()));

        doNothing().when(redisLikeCache).decrementLikes("postLikes:", postId);

        postLikeService.removeLike(postId, userId);

        verify(userServiceClient).getUser(userId);
        verify(postRepository).existsById(postId);
        verify(likeRepository).findByPostIdAndUserId(postId, userId);
        verify(redisLikeCache).decrementLikes("postLikes:", postId);
    }

    @Test
    @DisplayName("Тест удаления лайка из поста - ошибка, если лайк не найден")
    public void removeLike_NotFound() {
        Long postId = 1L;
        Long userId = 2L;

        when(likeRepository.findByPostIdAndUserId(postId, userId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> postLikeService.removeLike(postId, userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Лайк под этим постом не найден у пользователя с id: "
                        + userId + " id поста: " + postId);

        verify(likeRepository).findByPostIdAndUserId(postId, userId);
        verify(postRepository, never()).existsById(postId);
        verify(redisLikeCache, never()).decrementLikes("postLikes:", postId);
    }

}