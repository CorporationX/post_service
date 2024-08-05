package faang.school.postservice.exception.post;

import faang.school.postservice.exception.BaseRuntimeException;
import faang.school.postservice.exception.exceptionmessages.PostServiceExceptionMessage;

public class PostServiceException extends BaseRuntimeException {
    public PostServiceException(PostServiceExceptionMessage message, Object... args) {
        super(message.getMsg(), args);
    }
}
