package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentPublishedEvent;
import faang.school.postservice.dto.like.LikePostEvent;
import faang.school.postservice.dto.post.PostPublishedEvent;

public interface FeedService {
    void distributePostsToUsersFeeds(PostPublishedEvent event);

    void addNewComment(CommentPublishedEvent commentEvent);

    void addNewLike(LikePostEvent likePostEvent);
}
