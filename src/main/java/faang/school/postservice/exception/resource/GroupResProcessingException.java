package faang.school.postservice.exception.resource;

import faang.school.postservice.exception.BaseRuntimeException;
import faang.school.postservice.exception.GroupBaseRuntimeException;
import faang.school.postservice.exception.messages.PostServiceExceptionMessage;
import faang.school.postservice.exception.messages.ResourceServiceExceptionMessage;
import faang.school.postservice.exception.post.PostServiceException;

import java.util.Collection;

public class GroupResProcessingException extends GroupBaseRuntimeException {

    public GroupResProcessingException(Collection<BaseRuntimeException> exp) {
        super(
                exp,
                ResourceServiceExceptionMessage.GROUP_RESOURCE_PROCESSING_FAILED.getMessage()
        );
    }

    public GroupResProcessingException(
            Collection<BaseRuntimeException> exp,
            PostServiceException message, Object... args
    ) {
        super(exp, message.getMessage(), args);
    }
}
