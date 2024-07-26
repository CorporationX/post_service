package faang.school.postservice.exception.post;

public class PostAlreadyPublishedException extends RuntimeException {
    private static final String MESSAGE = "Сообщение уже опубликовано.";

    public PostAlreadyPublishedException() {
        super(MESSAGE);
    }
}
