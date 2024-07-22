package faang.school.postservice.service.feed.comment.async;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.CommentFeedDto;
import faang.school.postservice.service.feed.comment.CommentFeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
@Async("feedTaskExecutor")
public class AsyncCommentFeedServiceImpl implements AsyncCommentFeedService {

    private final CommentFeedService commentFeedService;
    private final UserContext userContext;

    @Override
    public CompletableFuture<List<CommentFeedDto>> getCommentsWithAuthors(long postId, long userId) {

        userContext.setUserId(userId);
        return CompletableFuture.completedFuture(commentFeedService.getCommentsWithAuthors(postId));
    }
}
