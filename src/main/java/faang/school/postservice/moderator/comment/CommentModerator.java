package faang.school.postservice.moderator.comment;

import faang.school.postservice.repository.CommentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CommentModerator {
    CommentRepository commentRepository;

    @Scheduled(cron = "0 0 8 * * *")
    public void checkComment() {
        commentRepository.getCommentsBy
    }
}
