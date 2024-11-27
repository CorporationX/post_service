package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentPublishedEvent;
import faang.school.postservice.dto.like.LikePostEvent;
import faang.school.postservice.dto.post.PostForFeedDto;
import faang.school.postservice.dto.post.PostPublishedEvent;
import faang.school.postservice.dto.post.PostViewEvent;
import faang.school.postservice.model.Feed;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface FeedService {
    void distributePostsToUsersFeeds(PostPublishedEvent event);

    void addNewComment(CommentPublishedEvent commentEvent);

    void addNewLike(LikePostEvent likePostEvent);

    void addNewView(PostViewEvent postViewEvent);

    CompletableFuture<Feed> generateFeedForUser(long userId);

    List<PostForFeedDto> getFeed(long userId, Long latestPostId) throws ExecutionException, InterruptedException;
}
