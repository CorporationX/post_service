package faang.school.postservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessages {
    WRONG_POST_ID("Passed post id %d does not match the post id %d in comment"),
    WRONG_AUTHOR_ID("Passed author id %d does not match the comment author id %d"),
    COMMENT_NOT_FOUND("Comment with id %d not found"),
    POST_DELETED_OR_NOT_PUBLISHED("Post with id %d was deleted or not published"),
    POST_NOT_FOUND("Post with id %d not found");

    private final String message;
}
