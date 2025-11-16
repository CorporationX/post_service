package faang.school.postservice.service.ai;

import faang.school.postservice.service.post.PostV2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostCorrecterService {

    private final PostV2Service postV2Service;

    @Scheduled(cron = "${post-correcter.cron}")
    public void correctDraftPosts() {
        postV2Service.correctDraftPosts();
    }
}
