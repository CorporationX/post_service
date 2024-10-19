package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.feed.RedisFeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {
    private final RedisFeedRepository redisFeedRepository;
    private final CacheService cacheService;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final PostMapper postMapper;

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
        List<PostDto> resultPostDtos = constructPostsForFeed(userId, lastSeenDate);
        Map<Long, UserDto> resultUsersMap = cacheService.fetchUsers(resultPostDtos);
        Map<Long, List<CommentDto>> resultCommentsMap = cacheService.fetchComments(resultPostDtos);

        return assembleFeedPosts(resultPostDtos, resultUsersMap, resultCommentsMap);
    }

    private List<PostDto> constructPostsForFeed(Long userId, LocalDateTime lastSeenDate) {
        List<PostDto> resultPostDtos = new ArrayList<>();
        LocalDateTime currentLastSeenDate = lastSeenDate;

        while (resultPostDtos.size() < pageSize) {
            List<Long> postIds = redisFeedRepository.getPostIds(userId, currentLastSeenDate, pageSize);
            if (postIds.isEmpty()) {
                break;
            }
            List<PostDto> postDtos = cacheService.fetchPosts(postIds);
            if (!postDtos.isEmpty()) {
                resultPostDtos.addAll(postDtos);
                currentLastSeenDate = getLastSeenDate(resultPostDtos);
            }
        }

        int quantityMissingPosts = pageSize - resultPostDtos.size();
        if (quantityMissingPosts > 0) {
            List<PostDto> missingPostsFromDB = fetchPostsFromDB(userId, quantityMissingPosts, currentLastSeenDate);
            resultPostDtos.addAll(missingPostsFromDB);
        }

        return resultPostDtos;
    }

    private LocalDateTime getLastSeenDate(List<PostDto> resultPostDtos) {
        return resultPostDtos.stream()
                .map(PostDto::getPublishedAt)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    private List<PostDto> fetchPostsFromDB(Long userId, int quantity, LocalDateTime lastSeenDate) {
        List<Long> followeeIds = userServiceClient.getFolloweeIdsByFollowerId(userId);
        List<Post> postsForFeed = postRepository.findPostsForFeed(followeeIds, lastSeenDate, quantity);
        return postsForFeed.stream()
                .map(postMapper::toDto)
                .toList();
    }

    private List<FeedPostDto> assembleFeedPosts(
            List<PostDto> postDtos,
            Map<Long, UserDto> resultUsersMap,
            Map<Long, List<CommentDto>> resultCommentsMap) {

        return postDtos.stream()
                .sorted(Comparator.comparing(PostDto::getPublishedAt).reversed())
                .flatMap(postDto -> {
                    UserDto author = resultUsersMap.get(postDto.getAuthorId());
                    if (author == null) {
                        log.warn("Can't create FeedPostDto, because author not found for postId: {} " +
                                "with authorId: {}. Skipping this post.", postDto.getId(), postDto.getAuthorId());
                        return Stream.empty();
                    } else {
                        FeedPostDto feedPostDto = FeedPostDto.builder()
                                .postDto(postDto)
                                .author(author)
                                .comments(resultCommentsMap.getOrDefault(postDto.getId(), Collections.emptyList()))
                                .build();
                        return Stream.of(feedPostDto);
                    }
                })
                .collect(Collectors.toList());
    }
}
