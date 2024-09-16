package faang.school.postservice.exception;

import java.util.ArrayList;
import java.util.Collection;

import faang.school.postservice.exception.resource.ResourceProcessingException;

public class GroupBaseRuntimeException extends BaseRuntimeException {

    /**
     * Must contain children only {@link ResourceProcessingException}
     */
    public final Collection<BaseRuntimeException> group;

    public GroupBaseRuntimeException(Collection<BaseRuntimeException> group, String message, Object... args) {
        super(message, args);
        this.group = group;
    }

    public GroupBaseRuntimeException(String message, Object... args) {
        super(message, args);
        this.group = new ArrayList<>();
    }
}
