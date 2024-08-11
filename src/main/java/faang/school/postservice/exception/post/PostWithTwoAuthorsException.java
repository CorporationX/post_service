package faang.school.postservice.exception.post;

public class PostWithTwoAuthorsException extends RuntimeException {
    private static final String MESSAGE = "Статья не может иметь двух авторов.";

    public PostWithTwoAuthorsException() {
        super(MESSAGE);
    }
}
