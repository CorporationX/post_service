package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.kafka.PostCreatedEvent;
import faang.school.postservice.model.chache.UserCache;
import faang.school.postservice.repository.cache.UserCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
   private final UserServiceClient userServiceClient;
   private final UserCacheRepository userCacheRepository;
    public void addPostToFollowers(PostCreatedEvent postCreatedEvent){
        List<Long> followersId = userServiceClient.getFollowerIds(postCreatedEvent.)
        List<UserCache>  followers = userCacheRepository.findAllById(postCreatedEvent.getFollowersId()).spliterator()
    }
}
