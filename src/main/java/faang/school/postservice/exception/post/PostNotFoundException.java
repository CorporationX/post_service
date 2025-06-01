package faang.school.postservice.exception.post;

import jakarta.persistence.EntityNotFoundException;

public class PostNotFoundException extends EntityNotFoundException {
    public PostNotFoundException(long postId) {
        super(String.format("Post with id %d not found", postId));
    }
}
