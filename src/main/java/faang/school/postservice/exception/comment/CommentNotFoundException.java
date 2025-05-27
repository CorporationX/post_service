package faang.school.postservice.exception.comment;

import java.util.NoSuchElementException;

public class CommentNotFoundException extends NoSuchElementException {

    public CommentNotFoundException(long commentId) {
        super(String.format("Comment with id %d not found", commentId));
    }
}
