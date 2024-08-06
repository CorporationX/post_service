package faang.school.postservice.exception.post;

import faang.school.postservice.exception.messages.PostServiceExceptionMessage;

public class PostAlreadyPublished extends PostServiceException{
    public PostAlreadyPublished(Long postId) {
        super(
                PostServiceExceptionMessage.POST_ALREADY_PUBLISHED,
                postId
        );
    }
}
