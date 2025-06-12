package faang.school.postservice.exception.comment;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(Long commentId) {
        super(String.format("Комментарий с id=%d не найден и не может быть удален", commentId));
    }
}
