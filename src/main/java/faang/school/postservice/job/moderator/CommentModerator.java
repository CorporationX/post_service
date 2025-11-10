package faang.school.postservice.job.moderator;

import faang.school.postservice.service.moderate.ModerateComments;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentModerator {

    private final ModerateComments moderateComments;

    @Scheduled(fixedDelayString = "${app.moderation.time:}")
    public void moderateComments() {
        moderateComments.moderateNewComments();
    }
}
