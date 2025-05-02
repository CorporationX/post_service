package faang.school.postservice.service.kafka.listener;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedKafkaListener {
    private final PostService postService;

}
