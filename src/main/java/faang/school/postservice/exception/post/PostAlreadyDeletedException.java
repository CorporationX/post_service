package faang.school.postservice.exception.post;

public class PostAlreadyDeletedException extends RuntimeException {
    private static final String MESSAGE = "Сообщение уже удалено.";

    public PostAlreadyDeletedException() {
        super(MESSAGE);
    }
}
