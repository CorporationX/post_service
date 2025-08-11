package faang.school.postservice.service.feed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.model.RedisPost;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final RedisPostMapper redisPostMapper;

    public void heat() {
        int authorsCount = userServiceClient.getAllAuthors();
        int subscribersCount = userServiceClient.getAllSubscribers();
        List<Integer> authors = userServiceClient.getAuthorsOrderdBySubscribers();
        Map<Long, List<RedisPost>> postsOfUser = new HashMap<>();
        Map<Long, List<UserDto>> followersOfUser = new HashMap<>();
        Map<Long, List<Long>> followersIdsOfUser = new HashMap<>();

        for (long userId : authors) {
            followersOfUser.computeIfAbsent(userId, k -> userServiceClient.getFollowers(userId));
            postsOfUser.computeIfAbsent(userId, k -> postRepository.findByAuthorId(userId)
                .stream()
                .map(redisPostMapper::toRedisPost)
                .toList()
            );
        }

        followersIdsOfUser = followersOfUser.entrySet().stream()
            .collect(Collectors.<Map.Entry<Long, List<UserDto>>, Long, List<Long>>toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream()
                    .map(UserDto::id)
                    .collect(Collectors.toList())
        ));        
        
        log.info("Total lines to process Authors {}.", authorsCount);
        log.info("Total lines to process Subscribers {}.", subscribersCount);
        log.info("List of authors by subscribers {}.", authors);
        log.info("Users list {}.", followersOfUser);
        log.info("Users and posts {}.", postsOfUser);
        log.info("users list ids {}.", followersIdsOfUser);
    }
}