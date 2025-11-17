package faang.school.postservice.service.ai;

import faang.school.postservice.service.post.PostV2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledCorrecterService {

    private final PostV2Service postV2Service;

    @Scheduled(cron = "${post-correcter.cron}")
    public void correctDraftPosts() {
        postV2Service.correctDraftPosts();
        log.info("Draft post correction job finished");
    }
}
