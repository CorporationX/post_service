package faang.school.postservice.validator;

import org.springframework.stereotype.Component;

@Component
public class LikeControllerValidator {

    public void validAddLikeToPost(long postId) {
        if (postId < 0) {
            throw new IllegalArgumentException("Post id not specified or negative");
        }
    }

    public void validAddLikeToComment(long commentId) {
        if (commentId < 0) {
            throw new IllegalArgumentException("Comment id not specified or negative");
        }
    }
}