package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final UserServiceClient userServiceClient;

}
