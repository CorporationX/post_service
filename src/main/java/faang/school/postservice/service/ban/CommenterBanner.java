package faang.school.postservice.service.ban;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.postservice.redis.message_broker.CommenterBannerPublisher;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommenterBanner {
    private final PostService postService;
    private final CommenterBannerPublisher commenterBannerPublisher;

    @Scheduled(cron = "${scheduling.commenter-banner.cron}")
    public void selectUsersForBan() throws JsonProcessingException {
        log.info("Начинается плановая проверка, кого из пользователей нужно забанить за оскорбительные комментарии.");
        List<Long> userIdsForBan = postService.selectUsersForBan();
        if (userIdsForBan.isEmpty()) {
            log.info("Не нашлось никого, кого нужно забанить.");
        } else {
            commenterBannerPublisher.publish(userIdsForBan);
            log.info("Список пользователей для банна сформирован и опубликован в брокере сообщений.");
        }
    }
}
