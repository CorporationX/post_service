package faang.school.postservice.scheduler;

import faang.school.postservice.config.redis.RedisMessagePublisher;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CommenterBanner {

    private final CommentService commentService;
    private final RedisMessagePublisher publisher;

    @Value("${spring.data.redis.channels.ban-channel.name}")
    private String userBanChannelName;

    @Async(value = "commenterBannerExecutor")
    @Scheduled(cron = "${commenter-banner.cron}")
    public void runBannerTask() {
        List<Long> authorIdsForBan = commentService.findAuthorIdsForBan();
        publisher.publish(userBanChannelName, authorIdsForBan);
    }
}
