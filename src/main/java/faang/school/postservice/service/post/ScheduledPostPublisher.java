package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScheduledPostPublisher {
    private final PostService postService;
    @Value("${post.publisher.sub-list-size}")
    private Integer subListSize;

    @Scheduled(cron = "${post.publisher.scheduler.cron}")
    public void scheduledPostPublish() {
        log.debug("Начало загрузки списка постов");
        var readyToPublishPosts = postService.getAllReadyToPublishPosts();
        log.debug("Список постов загружен");
        var subLists = splitPostListToSubLists(readyToPublishPosts, subListSize);
        subLists.forEach(postService::processSubList);
    }

    private List<List<Post>> splitPostListToSubLists(List<Post> posts, Integer subListSize) {
        return IntStream.range(0, posts.size())
                .filter(i -> i % subListSize == 0)
                .mapToObj(i -> posts.subList(i, Math.min(i + subListSize, posts.size())))
                .toList();
    }
}
