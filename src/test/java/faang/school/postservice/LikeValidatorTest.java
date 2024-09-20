package faang.school.postservice;

import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.validator.LikeValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class LikeValidatorTest {
    @InjectMocks
    LikeValidator likeValidator;

    @Test
    void validateLikeTest() {
        Like like = new Like();
        like.setUserId(1L);
        List<Like> likes = List.of(like);
        Post post = new Post();
        post.setLikes(likes);

        Assertions.assertThrows(DataValidationException.class,() -> likeValidator.validateLike(like, post));
    }

    @Test
    void validateLikedPost() {

    }

    @Test
    void validateLikedComment() {

    }
}
