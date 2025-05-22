package faang.school.postservice.service.like;

import faang.school.postservice.model.Like;

public interface LikeService {

    Like likeThePost(Like like, long postId);
}
