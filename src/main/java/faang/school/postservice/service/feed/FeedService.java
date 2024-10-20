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
import java.util.Objects;
import java.util.Set;
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
        List<PostDto> postDtos = constructPostsForFeed(userId, lastSeenDate);
        Map<Long, List<CommentDto>> commentsMap = cacheService.fetchComments(postDtos);

        Set<Long> userIds = prepareAuthorsIds(postDtos, commentsMap);
        Map<Long, UserDto> usersMap = cacheService.fetchUsers(userIds);

        return assembleFeedPosts(postDtos, usersMap, commentsMap);
    }

    private Set<Long> prepareAuthorsIds(List<PostDto> postDtos, Map<Long, List<CommentDto>> commentsMap) {
        return Stream.concat(
                postDtos.stream()
                        .map(PostDto::getAuthorId),
                commentsMap.values().stream()
                        .flatMap(List::stream)
                        .map(CommentDto::getAuthorId)
        ).collect(Collectors.toSet());
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
            Map<Long, UserDto> usersMap,
            Map<Long, List<CommentDto>> commentsMap) {

        return postDtos.stream()
                .sorted(Comparator.comparing(PostDto::getPublishedAt).reversed())
                .flatMap(postDto -> {
                    UserDto postAuthor = usersMap.get(postDto.getAuthorId());
                    if (postAuthor == null) {
                        log.warn("Can't create FeedPostDto, because author not found for postId: {} " +
                                "with authorId: {}. Skipping this post.", postDto.getId(), postDto.getAuthorId());
                        return Stream.empty();
                    } else {
                        List<CommentDto> comments = commentsMap.getOrDefault(postDto.getId(), Collections.emptyList())
                                .stream()
                                .filter(commentDto -> usersMap.containsKey(commentDto.getAuthorId()))
                                .toList();

                        List<UserDto> commentAuthors = getCommentAuthors(comments, usersMap);

                        FeedPostDto feedPostDto = FeedPostDto.builder()
                                .postDto(postDto)
                                .author(postAuthor)
                                .comments(comments)
                                .commentsAuthors(commentAuthors)
                                .build();
                        return Stream.of(feedPostDto);
                    }
                })
                .toList();
    }

    private List<UserDto> getCommentAuthors(List<CommentDto> comments, Map<Long, UserDto> usersMap) {
        return comments.stream()
                .map(commentDto -> {
                    UserDto commentAuthor = usersMap.get(commentDto.getAuthorId());
                    if (commentAuthor == null) {
                        log.warn("Author not found for commentId: {} with authorId: {}. Skipping this comment.",
                                commentDto.getId(), commentDto.getAuthorId());
                        return null;
                    }
                    return commentAuthor;
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
