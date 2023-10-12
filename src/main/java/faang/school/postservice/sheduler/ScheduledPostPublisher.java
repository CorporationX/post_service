package faang.school.postservice.sheduler;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledPostPublisher {
    private final PostService postService;
    private final ConcurrentHashMap<LocalDateTime, Set<PostDto>> postMap;
    @Value("${post.quantity_elements}")
    private int quantityElements;

    @Scheduled(cron = "${post.publisher.scheduler.cron}")
    public void publishPosts() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Set<PostDto> postDtoSet = postMap.get(now);
        if (postDtoSet != null) {
            publishPostsFromSet(postDtoSet, now);
        }
    }

    private void publishPostsFromSet(Set<PostDto> postDtoSet, LocalDateTime now) {
        List<CompletableFuture<Void>> futuresPush = new ArrayList<>();
        List<PostDto> currentGroup = new ArrayList<>();

        for (PostDto element : postDtoSet) {
            currentGroup.add(element);
            if (currentGroup.size() == quantityElements) {
                addDataToFuturesPush(futuresPush, new ArrayList<>(currentGroup));
                currentGroup.clear();
            }
        }

        if (!currentGroup.isEmpty()) {
            addDataToFuturesPush(futuresPush, new ArrayList<>(currentGroup));
        }

        CompletableFuture.allOf(futuresPush.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    postMap.remove(now);
                    log.info("publish posts");
                });
    }

    private void addDataToFuturesPush(List<CompletableFuture<Void>> futures, List<PostDto> currentGroup) {
        log.info("{}", currentGroup.get(0).getId());
        futures.add(CompletableFuture.runAsync(() -> {
            currentGroup.forEach(postDto -> {
                postService.publishPost(postDto.getId(), postDto.getAuthorId());
            });
        }));
    }

    @Scheduled(cron = "${post.cache.scheduler.cron}")
    public void updateCachePost() {
        List<PostDto> posts = postService.findAllPostsByTimeAndStatus();
        log.info("get {} posts from cache", posts.size());
        saveListToCache(posts);
    }

    private void saveListToCache(List<PostDto> posts) {
        List<CompletableFuture<Void>> futuresSave = new ArrayList<>();
        List<PostDto> currentGroup = new ArrayList<>();

        for (int i = 0; i < posts.size(); i += quantityElements) {
            int endIndex = Math.min(i + quantityElements, posts.size());
            currentGroup = posts.subList(i, endIndex);
            addDataToFuturesSave(futuresSave, new ArrayList<>(currentGroup));
        }

        CompletableFuture.allOf(futuresSave.toArray(new CompletableFuture[0]))
                .thenRun(() -> log.info("update cache post"));
    }

    private void addDataToFuturesSave(List<CompletableFuture<Void>> futures, List<PostDto> currentGroup) {
        futures.add(CompletableFuture.runAsync(() -> {
            currentGroup.forEach(postDto -> {
                LocalDateTime postTime = postDto.getScheduledAt().truncatedTo(ChronoUnit.MINUTES);
                postMap.putIfAbsent(postTime, new HashSet<>());
                postMap.get(postTime).add(postDto);
            });
        }));
    }
}
