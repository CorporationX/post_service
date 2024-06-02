package faang.school.postservice.exception.like;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LikeOperatingExceptionMessage {
    NON_EXISTING_POST_EXCEPTION("Post for the passed id doesn't exist in system."),
    NON_EXISTING_COMMENT_EXCEPTION("Comment for the passed id doesn't exist in system.");

    private final String message;
}
