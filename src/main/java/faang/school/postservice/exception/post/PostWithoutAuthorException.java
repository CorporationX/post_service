package faang.school.postservice.exception.post;

public class PostWithoutAuthorException extends RuntimeException {
    private static final String MESSAGE = "У сообщения отсутствует автор.";

    public PostWithoutAuthorException() {
        super(MESSAGE);
    }
}
