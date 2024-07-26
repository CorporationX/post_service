package faang.school.postservice.exception.post;

public class PostWOAuthorException extends RuntimeException {
    private static final String MESSAGE = "У сообщения отсутствует автор.";

    public PostWOAuthorException() {
        super(MESSAGE);
    }
}
