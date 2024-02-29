package faang.school.postservice.service.hash;

import faang.school.postservice.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedHeaterService {
    private final UserServiceClient userServiceClient;
    private final AsyncFeedHeaterService asyncFeedHeaterService;

    @Value("${feed.user_batch}")
    private int userBatch;

    public void heat() {
        List<Long> userIds = userServiceClient.getUserIds();
        List<List<Long>> followerIdBatches = ListUtils.partition(userIds, userBatch);
        followerIdBatches.forEach(asyncFeedHeaterService::publishBatchPost);
    }
}
