package faang.school.postservice.exception.post;

import java.util.EntityNotFoundException;

public class PostNotFoundException extends EntityNotFoundException {
    public PostNotFoundException(String message) {
        super(message);
    }

    public PostNotFoundException(long postId) {
        super(String.format("Post with id %d not found", postId));
    }
}
