package faang.school.postservice.validator.like;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import faang.school.postservice.exception.like.LikeValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PostLikeValidationTest {

  @Mock
  private LikeRepository likeRepository;

  @Mock
  private PostRepository repository;

  @InjectMocks
  private PostLikeValidation postLikeValidation;

  @Test
  void testValidateLikes() {
    when(repository.findById(anyLong())).thenReturn(Optional.of(getPost()));
    when(likeRepository.findByPostIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

    postLikeValidation.validateLikes(1L, 1L);

    verify(repository).findById(anyLong());
    verify(likeRepository, times(2)).findByPostIdAndUserId(anyLong(), anyLong());
  }

  @Test
  void testNotValidateLikes() {
    when(repository.findById(anyLong())).thenReturn(Optional.of(getPost()));
    when(likeRepository.findByPostIdAndUserId(1L, 1L)).thenReturn(Optional.of(getLike()));

    assertThrows(LikeValidationException.class, () -> postLikeValidation.validateLikes(1L, 1L));

    verify(repository).findById(anyLong());
    verify(likeRepository).findByPostIdAndUserId(anyLong(), anyLong());
  }

  @Test
  void testCheckExistRecord() {
    when(repository.findById(1L)).thenReturn(Optional.of(getPost()));
    postLikeValidation.checkExistRecord(1L);
    assertThat(repository.findById(1L)).isNotEmpty();
  }

  @Test
  void testExceptionOnCheckExistRecord() {
    when(repository.findById(1L)).thenThrow(new LikeValidationException("error"));
    assertThrows(LikeValidationException.class, () -> postLikeValidation.checkExistRecord(1L));
  }

  private Post getPost() {
    return Post.builder()
        .id(1L)
        .build();
  }

  private static Like getLike() {
    return Like.builder()
        .id(1L)
        .userId(1L)
        .comment(Comment.builder()
            .id(1L)
            .build())
        .post(Post.builder()
            .id(1L)
            .build())
        .createdAt(LocalDateTime.of(2024, Month.AUGUST, 24,0, 0))
        .build();
  }

}