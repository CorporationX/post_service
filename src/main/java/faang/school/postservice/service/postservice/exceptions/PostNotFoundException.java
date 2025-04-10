package faang.school.postservice.service.postservice.exceptions;

import lombok.Getter;

@Getter
public class PostNotFoundException extends RuntimeException {
    private final long postId;

    public PostNotFoundException(long postId) {
        super("Пост с ID " + postId + " не найден.");
        this.postId = postId;
    }
}