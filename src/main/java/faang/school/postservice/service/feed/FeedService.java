package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedService {
    private final UserServiceClient userServiceClient;
    public void createAndSendPostEvent(Long postId, Long authorId) {
        List<Long> subscribrsIds = userServiceClient
    }
}
