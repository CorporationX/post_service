package faang.school.postservice.validator.like;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.entity.Comment;
import faang.school.postservice.model.entity.Like;
import faang.school.postservice.model.entity.Likeable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class LikeValidatorTest {
    @InjectMocks
    private LikeValidator likeValidator;

    @Mock
    private JpaRepository<Likeable, Long> repository;

    @Mock
    private Likeable likeable;

    private Comment comment;
    private long commentAndPostId;
    private long userId;

    @BeforeEach
    void setUp() {
        likeValidator = new LikeValidator();
        comment = new Comment();
        commentAndPostId = 1;
        userId = 1L;

    }

    @Test
    void givenValidWhenValidateThenReturnCommentAndPost() {
        Mockito.when(repository.findById(commentAndPostId)).thenReturn(Optional.of(likeable));

        var result = likeValidator.validate(commentAndPostId, userId, repository);

        Mockito.verify(repository).findById(commentAndPostId);
        Mockito.verify(likeable).getLikes();
        Assertions.assertEquals(likeable, result);
    }

    @Test
    void givenNotValidWhenValidateThenReturnException() {
        Mockito.when(repository.findById(commentAndPostId)).thenReturn(Optional.empty());

        Assertions.assertThrows(DataValidationException.class, () ->
                likeValidator.validate(commentAndPostId, userId, repository));

        Mockito.verify(repository).findById(commentAndPostId);
    }

    @Test
    void givenValidWhenValidateCommentOrPostThenReturnCommentAndPost() {
        Mockito.when(repository.findById(commentAndPostId)).thenReturn(Optional.of(likeable));

        var result = likeValidator.validateCommentOrPost(commentAndPostId, repository);

        Mockito.verify(repository).findById(commentAndPostId);
        Assertions.assertEquals(likeable, result);
    }

    @Test
    void givenNotValidWhenValidateCommentOrPostThenReturnThenReturnException() {
        Mockito.when(repository.findById(commentAndPostId)).thenReturn(Optional.empty());

        Assertions.assertThrows(DataValidationException.class, () ->
                likeValidator.validate(commentAndPostId, userId, repository));

        Mockito.verify(repository).findById(commentAndPostId);
    }

    @Test
    void givenValidWhenCheckingExistingLikeThenReturnSuccess() {
        Mockito.when(likeable.getLikes()).thenReturn(List.of());

        likeValidator.checkingExistingLike(likeable, userId);

        Mockito.verify(likeable).getLikes();
    }

    @Test
    void givenNotValidWhenCheckingExistingLikeThenReturnException() {
        Like like = new Like();
        like.setUserId(userId);
        Mockito.when(likeable.getLikes()).thenReturn(List.of(like));

        Assertions.assertThrows(DataValidationException.class, () ->
                likeValidator.checkingExistingLike(likeable, userId));


        Mockito.verify(likeable).getLikes();
    }
}