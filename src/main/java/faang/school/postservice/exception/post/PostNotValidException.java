package faang.school.postservice.exception.post;

public class PostNotValidException extends RuntimeException {
    public PostNotValidException() {
        super("Only one of authorId or projectId must be specified");
    }
}
