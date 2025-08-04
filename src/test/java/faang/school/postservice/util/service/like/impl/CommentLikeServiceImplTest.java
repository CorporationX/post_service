package faang.school.postservice.util.service.like.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.dto.user.UserClientDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.like.impl.CommentLikeServiceImpl;
import faang.school.postservice.service.like.kafka.KafkaLikePublisher;
import faang.school.postservice.service.like.redis.RedisLikeCache;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.junit.jupiter.Container;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;


import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты CommentLikeServiceImpl")
public class CommentLikeServiceImplTest {

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
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentLikeServiceImpl commentLikeServiceImpl;

    @Test
    @DisplayName("CommentLikeServiceImpl - addLike - Успешное добавление лайка к комментарию")
    public void addLike_Success() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .username("testUser")
                .email("dsfsdf")
                .build();
        LocalDateTime testTime = LocalDateTime.of(2023, 10, 1, 12, 0);

        when(likeRepository.findByCommentIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(Comment.builder().id(1L).build()));
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        doNothing().when(redisLikeCache).incrementLikes("comment_likes:", 1L);

        commentLikeServiceImpl.addLike(1L, 1L);

        verify(likeRepository).findByCommentIdAndUserId(1L, 1L);
        verify(commentRepository).findById(1L);
        verify(userServiceClient).getUser(1L);
        verify(redisLikeCache).incrementLikes("comment_likes:", 1L);

    }

    @Test
    @DisplayName("CommentLikeServiceImpl - addLike - Ошибка при добавлении лайка к комментарию, если лайк уже существует")
    public void addLike_AlreadyExists() {
        when(likeRepository.findByCommentIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(Like.builder().id(1L).build()));

        assertThatThrownBy(() -> commentLikeServiceImpl.addLike(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Лайк под этим комментарием уже оставлен пользователем с id: 1 id комментария: 1");
    }

    @Test
    @DisplayName("CommentLikeServiceImpl - removeLike - Успешное удаление лайка из комментария")
    public void removeLike_Success() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .username("testUser")
                .email("dsfsdf")
                .build();

        when(likeRepository.findByCommentIdAndUserId(1L, 1L)).thenReturn(Optional.of(Like.builder().id(1L).build()));
        when(commentRepository.existsById(1L)).thenReturn(true);
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        doNothing().when(redisLikeCache).decrementLikes("comment_likes:", 1L);

        commentLikeServiceImpl.removeLike(1L, 1L);

        verify(likeRepository).findByCommentIdAndUserId(1L, 1L);
        verify(commentRepository).existsById(1L);
        verify(userServiceClient).getUser(1L);
        verify(redisLikeCache).decrementLikes("comment_likes:", 1L);
    }
}