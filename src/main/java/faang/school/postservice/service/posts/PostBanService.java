package faang.school.postservice.service.posts;

import faang.school.postservice.messages.redis.publishers.Publisher;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostBanService {
    private final PostRepository postRepository;
    private final Publisher publisher;
    private final ChannelTopic userBanTopic;
    @Value("${scheduler.posts.ban-threshold}")
    private int banThreshold;

    public void publishBanCandidates() {
        List<Long> userIds = postRepository.findUsersToBanForPosts(banThreshold);

        if (userIds.isEmpty()) {
            return;
        }

        try {
            publisher.publish(userBanTopic, userIds);
        } catch (Exception e) {
            log.error("Failed to publish user ban list", e);
        }
    }
}

