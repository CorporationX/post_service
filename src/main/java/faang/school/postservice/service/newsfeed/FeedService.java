package faang.school.postservice.service.newsfeed;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.newsfeed.FeedDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.rediscache.FeedRedisRepository;
import faang.school.postservice.repository.rediscache.PostRedisRepository;
import faang.school.postservice.repository.rediscache.UserRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {

    private final FeedRedisRepository feedRedisRepository;
    private final UserRedisRepository userRedisRepository;
    private final PostRedisRepository postRedisRepository;
    private final PostMapper postMapper;

    public FeedDto getFeed(String userId, String postId) {
        log.info("Received a request to fetch the feed for the user with ID: {}", userId);
        List<String> postIds = feedRedisRepository.getPostIdsFromCache(userId, postId);
        List<UserDto> users = new ArrayList<>();
        List<PostDto> posts = new ArrayList<>();

        for (String id : postIds) {
            Post post = postRedisRepository.findPostByKey(postId);
            users.add(userRedisRepository.findUserByKey(post.getAuthorId().toString()));
            posts.add(postMapper.toDto(post));
            log.info("Post with ID: {} was fetched from Redis", post.getId().toString());
        }

        return FeedDto.builder()
                .userDtos(users)
                .posts(posts)
                .build();
    }
}
