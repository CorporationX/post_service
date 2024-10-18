package faang.school.postservice.service.feed;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.feed.RedisFeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {
    private final RedisFeedRepository redisFeedRepository;
    private final CacheService cacheService;

    @Value("${spring.data.redis.cache.feed.pageSize}")
    private int pageSize;

    public void addPostToFeed(List<Long> subscribersIds, Long postId, LocalDateTime publishedAt) {
        redisFeedRepository.addPost(subscribersIds, postId, publishedAt);
    }

    public void handlePostDeletion(Long postId) {
        cacheService.handlePostDeletion(postId);
        redisFeedRepository.deletePostFromAllFeeds(postId);
    }

    public List<FeedPostDto> getFeed(Long userId, LocalDateTime lastSeenDate) {
        List<Long> postIds = redisFeedRepository.getPostIds(userId, lastSeenDate);

        List<PostDto> postDtos = cacheService.fetchPosts(postIds);
        Map<Long, UserDto> resultUsersMap = cacheService.fetchUsers(postDtos);
        Map<Long, List<CommentDto>> resultCommentsMap = cacheService.fetchComments(postDtos);

        return assembleFeedPosts(postDtos, resultUsersMap, resultCommentsMap);
    }

    private List<FeedPostDto> assembleFeedPosts(
            List<PostDto> postDtos,
            Map<Long, UserDto> resultUsersMap,
            Map<Long, List<CommentDto>> resultCommentsMap) {

        List<FeedPostDto> result = new ArrayList<>();
        for (PostDto postDto : postDtos) {
            List<CommentDto> commentDtos = new ArrayList<>();
            if (resultCommentsMap.containsKey(postDto.getId())) {
                commentDtos = resultCommentsMap.get(postDto.getId());
            }
            FeedPostDto feedPostDto = FeedPostDto.builder()
                    .postDto(postDto)
                    .author(resultUsersMap.get(postDto.getAuthorId()))
                    .comments(commentDtos)
                    .build();
            result.add(feedPostDto);
        }
        return result;
    }
}
