package faang.school.postservice.service.ai;

import faang.school.postservice.service.post.PostV2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledCorrecterService {

    private final AiPostCorrectionService aiPostCorrectionService;

    @Scheduled(cron = "${post-correcter.cron}")
    public void runCorrectionJob() {
        log.info("Starting draft post correction job...");
        aiPostCorrectionService.correctDraftPosts();
        log.info("Draft post correction job completed");
    }
}

