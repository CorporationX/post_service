package faang.school.postservice.job.moderation;

import faang.school.postservice.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SwearingPostModerator extends PostModeratorBase {

    private final List<String> swearingDictionary;

    public SwearingPostModerator(@Value("${post.moderation.dictionary.swearing}") List<String> swearingDictionary) {
        this.swearingDictionary = swearingDictionary;
    }

    @Override
    public void handle(Post post) {
        log.info("{}: swearing moderating post ID: {}", Thread.currentThread().getName(), post.getId());
        post.setVerified(swearingDictionary.stream()
                .noneMatch(post.getContent()::contains));
        super.handle(post);
    }
}
