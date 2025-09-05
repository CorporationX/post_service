package faang.school.postservice.service.feed;

import faang.school.postservice.cache.user.FeedCache;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedRequest;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NewsFeedServiceImpl implements NewsFeedService {
    @Value("${spring.data.redis.cache.feed.per-page}")
    private int perPage;
    private final FeedCache feedCache;
    private final UserContext userContext;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Override
    public List<PostDto> getUserFeed(FeedRequest request) {
        long userId = userContext.getUserId();
        List<PostDto> userFeed = feedCache.getUserFeed(userId, request, perPage);
        int remaining = perPage - userFeed.size();

        if (remaining > 0 && (request.searchAfter() != null || !userFeed.isEmpty())) {
            Long lastId = userFeed.isEmpty() ? request.searchAfter() : userFeed.get(userFeed.size() - 1).id();
            List<Post> postsAfterId = postRepository.getPostsAfterIdForFollower(lastId, remaining);
            userFeed.addAll(postMapper.toPostDtoList(postsAfterId));
        }

        return userFeed;
    }


}
