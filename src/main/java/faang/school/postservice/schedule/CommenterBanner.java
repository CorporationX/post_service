package faang.school.postservice.schedule;

import faang.school.postservice.service.comment.UserBanPublisherImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class CommenterBanner {
    private final UserBanPublisherImpl commenterService;

    @Scheduled(cron = "${comment.ban-user-invoke}")
    public void banUser() {
        commenterService.banUserComment();
    }
}