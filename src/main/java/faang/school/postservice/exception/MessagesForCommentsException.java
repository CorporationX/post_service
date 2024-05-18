package faang.school.postservice.exception;

import lombok.Getter;

@Getter
public enum MessagesForCommentsException {
    NO_USER_IN_DB("There is no such user in database"),
    NO_POST_IN_DB("There is no post in database"),
    NO_COMMENT_IN_DB("There is no comment in db"),
    NO_COMMENTS_IN_THE_POST("There are no comments");

    private final String message;

    MessagesForCommentsException(String message) {
        this.message = message;
    }
}
