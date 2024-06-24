package faang.school.postservice.exception;

import lombok.Getter;

@Getter
public enum MessagesForCommentsException {

    NO_USER_IN_DB("There is no such user in database"),

    NO_POST_IN_DB("There is no post in database"),

    NO_COMMENT_IN_DB("There is no comment in db"),

    NO_COMMENTS_IN_THE_POST("There are no comments"),

    POST_ID_IS_INCORRECT("Post id cannot be 0 or less"),

    ID_IS_NULL("Id in CommentDto can not be null");

    private final String message;

    MessagesForCommentsException(String message) {
        this.message = message;
    }
}
