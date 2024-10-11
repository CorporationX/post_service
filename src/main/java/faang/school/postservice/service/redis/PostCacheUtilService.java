package faang.school.postservice.service.redis;

import faang.school.postservice.dto.comment.LastCommentDto;
import faang.school.postservice.model.redis.RedisPost;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostCacheUtilService {
    private final UserCacheService userCacheService;

    public String getUserName(Long authorId) {
        log.info("getUserName authorId: {}", authorId);
        return userCacheService
                .findUserById(authorId)
                .getUserInfo()
                .getUsername();
    }

    @Async
    public void setLastComments(RedisPost redisPost) {
        log.info("setting LastComments to redisPost: {}", redisPost);
        LinkedHashSet<LastCommentDto> lastComments = redisPost.getPostInfoDto().getComments();
        lastComments = lastComments.stream().peek(comment -> {
            String commentAuthorName = getUserName(comment.getAuthorId());
            comment.setAuthor(commentAuthorName);
        }).collect(Collectors.toCollection(LinkedHashSet::new));
        redisPost.getPostInfoDto().setComments(lastComments);
        log.info("Comments were added to redisPost: {}", redisPost);
    }

    public RedisPost getFilledRedisPost(RedisPost redisPost) {
        redisPost.getPostInfoDto().getDto().setUsername(getUserName(redisPost.getId()));
        setLastComments(redisPost);
        return redisPost;
    }
}
