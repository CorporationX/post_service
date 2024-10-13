package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeRequestDto;
import faang.school.postservice.dto.like.LikeResponseDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.publisher.LikePostEventPublisher;
import faang.school.postservice.validator.like.LikeValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для LikeService")
public class LikeServiceTest {

    private final long userId = 1L;
    private final long postId = 1L;
    private final long commentId = 1L;

    private LikeRequestDto likeRequestDto;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikeMapper likeMapper;

    @Mock
    private LikeValidator likeValidator;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private LikePostEventPublisher likePostEventPublisher;

    @InjectMocks
    private LikeService likeService;

    @BeforeEach
    public void setup() {
        likeRequestDto = new LikeRequestDto();
        likeRequestDto.setUserId(userId);
    }

    @Nested
    @DisplayName("Позитивные тесты")
    class PositiveTests {

        @Test
        @DisplayName("Должен добавить лайк для поста")
        void shouldAddLikeToPost() {
            likeRequestDto.setPostId(postId);

            Post post = new Post();
            when(postRepository.findById(likeRequestDto.getPostId())).thenReturn(Optional.of(post));

            Like like = new Like();
            when(likeMapper.toEntity(likeRequestDto)).thenReturn(like);
            LikeResponseDto likeResponseDto = new LikeResponseDto();
            when(likeMapper.toResponseDto(like)).thenReturn(likeResponseDto);

            // Мокаем вызов валидатора и проверки пользователя
            doNothing().when(likeValidator).validateLikeForPostExists(likeRequestDto.getPostId(), likeRequestDto.getUserId());
            when(userServiceClient.getUser(likeRequestDto.getUserId())).thenReturn(null); // Предполагаем, что пользователь существует

            LikeResponseDto result = likeService.addLike(likeRequestDto);

            verify(likeRepository).save(like);
            verify(postRepository).findById(likeRequestDto.getPostId());
            verify(likeValidator).validateLikeForPostExists(likeRequestDto.getPostId(), likeRequestDto.getUserId());
            verify(userServiceClient).getUser(likeRequestDto.getUserId());
            verify(likePostEventPublisher).publishEvent(any());
            assertEquals(likeResponseDto, result);
        }


        @Test
        @DisplayName("Должен добавить лайк для комментария")
        void shouldAddLikeToComment() {
            likeRequestDto.setCommentId(commentId);

            Comment comment = new Comment();
            when(commentRepository.findById(likeRequestDto.getCommentId())).thenReturn(Optional.of(comment));

            Like like = new Like();
            when(likeMapper.toEntity(likeRequestDto)).thenReturn(like);
            LikeResponseDto likeResponseDto = new LikeResponseDto();
            when(likeMapper.toResponseDto(like)).thenReturn(likeResponseDto);

            // Мокаем вызов валидатора и проверки пользователя
            doNothing().when(likeValidator).validateLikeForCommentExists(likeRequestDto.getCommentId(), likeRequestDto.getUserId());
            when(userServiceClient.getUser(likeRequestDto.getUserId())).thenReturn(null); // Предполагаем, что пользователь существует

            LikeResponseDto result = likeService.addLike(likeRequestDto);

            verify(likeRepository).save(like);
            verify(commentRepository).findById(likeRequestDto.getCommentId());
            verify(likeValidator).validateLikeForCommentExists(likeRequestDto.getCommentId(), likeRequestDto.getUserId());
            verify(userServiceClient).getUser(likeRequestDto.getUserId());
            assertEquals(likeResponseDto, result);
        }


        @Test
        @DisplayName("Должен удалить лайк по ID")
        void shouldRemoveLikeById() {
            long likeId = 1L;
            when(likeRepository.existsById(likeId)).thenReturn(true);  // Мокаем существование лайка

            likeService.removeLike(likeId);

            verify(likeRepository).deleteById(likeId);
        }
    }

    @Nested
    @DisplayName("Негативные тесты")
    class NegativeTests {

        @Test
        @DisplayName("Должен выбросить исключение, если пост не найден при добавлении лайка")
        void shouldThrowExceptionWhenPostNotFound() {
            likeRequestDto.setPostId(postId);

            when(postRepository.findById(likeRequestDto.getPostId())).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                likeService.addLike(likeRequestDto);
            });

            verify(postRepository).findById(likeRequestDto.getPostId());
            verify(likeRepository, never()).save(any());
            assertEquals("Post with ID " + postId + " not found", exception.getMessage());
        }

        @Test
        @DisplayName("Должен выбросить исключение, если комментарий не найден при добавлении лайка")
        void shouldThrowExceptionWhenCommentNotFound() {
            likeRequestDto.setCommentId(commentId);

            when(commentRepository.findById(likeRequestDto.getCommentId())).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                likeService.addLike(likeRequestDto);
            });

            verify(commentRepository).findById(likeRequestDto.getCommentId());
            verify(likeRepository, never()).save(any());
            assertEquals("Comment with ID " + commentId + " not found", exception.getMessage());
        }

        @Test
        @DisplayName("Должен выбросить исключение, если пользователь не найден")
        void shouldThrowExceptionWhenUserNotFound() {
            when(userServiceClient.getUser(userId)).thenThrow(new EntityNotFoundException("User not found with ID: " + userId));

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                likeService.addLike(likeRequestDto);
            });

            verify(userServiceClient).getUser(userId);
            assertEquals("User not found with ID: " + userId, exception.getMessage());
        }
    }
}
