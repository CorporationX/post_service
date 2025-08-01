package faang.school.postservice.aspect;

import faang.school.postservice.annotation.CachePublishedPost;
import faang.school.postservice.annotation.CacheUpdatePost;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.feed.FeedCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Aspect
public class CachePostAspect {

    private final FeedCacheService feedCacheService;
    private final UserServiceClient userServiceClient;

    @AfterReturning(value = "@annotation(publishedPost)", returning = "post")
    public void cachePublishedPost(CachePublishedPost publishedPost, ResponsePostDto post) {
        boolean authorNotCached = !feedCacheService.checkUserSaved(post.getAuthorId());
        if (authorNotCached) {
            UserDto userDto = userServiceClient.getUser(post.getAuthorId());
            feedCacheService.saveUser(userDto);
        }
        feedCacheService.savePost(post);
    }

    @AfterReturning(value = "@annotation(updatePost)", returning = "post")
    public void updateCachedPost(CacheUpdatePost updatePost, ResponsePostDto post) {
        feedCacheService.savePost(post);
    }

    //TODO добавить метод для добавления id нового поста в feed всех подписчиков
}
