package faang.school.postservice.service.feed;

import java.util.List;

import org.springframework.stereotype.Service;

import faang.school.postservice.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final UserServiceClient userServiceClient;

    public void heat() {
        int totalSubscriptions = userServiceClient.getAllSubscriptions();
        List<Integer> authors = userServiceClient.getAuthorsOrderdBySubscribers();

        log.info("Total lines to process {}.", totalSubscriptions);
        log.info("List of authors by subscribers {}.", authors);
    }
}