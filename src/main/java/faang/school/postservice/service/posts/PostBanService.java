package faang.school.postservice.service.posts;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.messages.redis.publishers.Publisher;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostBanService {
    private final PostRepository postRepository;
    private final Publisher publisher;
    @Value("${scheduler.posts.ban-threshold}")
    private int banThreshold;
    @Value("${scheduler.posts.batch-size}")
    private int batchSize;
    private final ChannelTopic userBanTopic;

    public void publishBanCandidates() {
        int page = 0;
        List<Long> userIds;

        do {
            userIds = postRepository.findUsersToBanForPosts(banThreshold, PageRequest.of(page, batchSize));

            if (userIds.isEmpty()) {
                return;
            }

            try {
                publisher.publish(userBanTopic, userIds);
            } catch (Exception e) {
                log.error("Failed to publish user ban list", e);
            }

            page++;
        } while (!userIds.isEmpty());
    }
}

