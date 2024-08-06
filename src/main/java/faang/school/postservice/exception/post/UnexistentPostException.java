package faang.school.postservice.exception.post;

import faang.school.postservice.exception.messages.PostServiceExceptionMessage;

public class UnexistentPostException extends PostServiceException {
    public UnexistentPostException(long postId){
        super(
                PostServiceExceptionMessage.POST_DOESNT_EXIST,
                postId
        );
    }
}
