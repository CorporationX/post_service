package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event_broker.PostEvent;
import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostEventPublisherService {
    private final UserServiceClient userServiceClient;

    @Value("${feed.post_batch}")
    private int batchSize;

    public List<List<Long>> getFollowerIdBatches(PostEvent originalEvent) {
        long followeeId = (originalEvent.getUserAuthorId() != null) ? originalEvent.getUserAuthorId() : originalEvent.getProjectAuthorId();
        List<Long> followerIds = userServiceClient.getFollowerIds(followeeId);
        List<List<Long>> followerIdBatches = ListUtils.partition(followerIds, batchSize);

        UserDto userDto = userServiceClient.getUser(originalEvent.getUserAuthorId());
        originalEvent.setUserDtoAuthor(userDto);
        return followerIdBatches;
    }
}
