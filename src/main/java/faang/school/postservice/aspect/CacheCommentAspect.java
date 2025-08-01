package faang.school.postservice.aspect;

import faang.school.postservice.annotation.CacheCreateComment;
import faang.school.postservice.annotation.CacheUpdateComment;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDtoResponse;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.feed.FeedCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
@Aspect
public class CacheCommentAspect {

    private final FeedCacheService feedCacheService;
    private final UserServiceClient userServiceClient;

    @AfterReturning(value = "@annotation(createComment)", returning = "comment")
    public void cacheNewComment(CacheCreateComment createComment, CommentDtoResponse comment) {
        boolean authorNotCached = !feedCacheService.checkUserSaved(comment.getAuthorId());
        if(authorNotCached) {
            UserDto userDto = userServiceClient.getUser(comment.getAuthorId());
            feedCacheService.saveUser(userDto);
        }
        feedCacheService.saveComment(comment);
        feedCacheService.saveCommentToFeed(
                comment.getPostId(),
                comment.getCommentId(),
                LocalDateTime.parse(comment.getCreateData(), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
        );
    }

    @AfterReturning(value = "@annotation(updateComment)", returning = "comment")
    public void cacheUpdatedComment(CacheUpdateComment updateComment, CommentDtoResponse comment) {
        feedCacheService.saveComment(comment);
    }
}
