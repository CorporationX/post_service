package faang.school.postservice.job.moderation;


import faang.school.postservice.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OtherAPostModerator extends PostModeratorBase {

    @Override
    public void handle(Post post) {
        log.info("{}: otherA moderating pos ID: {}", Thread.currentThread().getName(), post.getId());
        super.handle(post);
    }
}
