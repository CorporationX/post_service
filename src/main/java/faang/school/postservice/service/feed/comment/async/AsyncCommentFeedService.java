package faang.school.postservice.service.feed.comment.async;

import faang.school.postservice.dto.feed.CommentFeedDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AsyncCommentFeedService {

    CompletableFuture<List<CommentFeedDto>> getCommentsWithAuthors(long postId, long userId);
}
