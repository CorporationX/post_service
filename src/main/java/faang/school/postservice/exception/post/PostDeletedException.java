package faang.school.postservice.exception.post;

import faang.school.postservice.exception.messages.PostServiceExceptionMessage;

public class PostDeletedException extends PostServiceException {
    public PostDeletedException(PostServiceExceptionMessage message, Object... args) {
        super(message, args);
    }
}
