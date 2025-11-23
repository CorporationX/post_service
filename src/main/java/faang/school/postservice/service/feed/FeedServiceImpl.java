package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.CommentFeedDto;
import faang.school.postservice.dto.feed.PostFeedDto;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.dto.redis.UserRedisDto;
import faang.school.postservice.dto.user.UserViewDto;
import faang.school.postservice.mapper.FeedMapper;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.CommentRedisRepository;
import faang.school.postservice.repository.redis.FeedRedisRepository;
import faang.school.postservice.repository.redis.PostRedisRepository;
import faang.school.postservice.repository.redis.UserRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Сервис для взаимодействия с feed пользователя
 *
 * @author Linempy
 * @since 28.09.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final UserServiceClient userClient;
    private final FeedMapper feedMapper;
    private final UserMapper userMapper;
    private final UserContext userContext;
    private final PostRepository postRepository;
    private final FeedRedisRepository feedRedisRepository;
    private final PostRedisRepository postRedisRepository;
    private final UserRedisRepository userRedisRepository;
    private final CommentRedisRepository commentRedisRepository;

    @Value("${feed.feed-size}")
    private int feedSize;

    public List<PostFeedDto> getFeed(Long lastPostId) {
        List<Long> postIds = getFeedFromDatabase(lastPostId);

        List<PostRedisDto> posts = getPostsWithFallback(postIds);

        List<Long> userIds = posts.stream()
                .map(PostRedisDto::authorId)
                .distinct()
                .toList();

        List<UserRedisDto> authors = getUserRedisDtosWithFallback(userIds);

        Map<Long, UserRedisDto> authorMap = authors.stream()
                .collect(Collectors.toMap(UserRedisDto::id, user -> user));

        List<PostFeedDto> result = posts.stream()
                .map(post -> {
                    UserRedisDto author = authorMap.get(post.authorId());

                    List<CommentFeedDto> comments = commentRedisRepository.getLatestComments(post.latestComments());
                    return feedMapper.toFeedDto(post, author, comments);
                })
                .toList();

        log.info("Feed для пользователя был успешно сформирован");
        return result;
    }

    private List<Long> getFeedFromDatabase(Long lastPostId) {
        List<Long> postIds = feedRedisRepository.getFeed(lastPostId, userContext.getUserId(), feedSize);

        if (postIds.size() < feedSize) {
            List<Long> additionalPostIds;
            if (lastPostId == null) {
                additionalPostIds = postRepository.getFirstFeedOfUser(userContext.getUserId(), feedSize);
            } else {
                additionalPostIds = postRepository.getFeedAfterPost(lastPostId, userContext.getUserId(), feedSize);
            }

            return Stream.concat(postIds.stream(), additionalPostIds.stream())
                    .distinct()
                    .toList();
        }

        return postIds;
    }

    private List<PostRedisDto> getPostsWithFallback(List<Long> postIds) {
        List<PostRedisDto> posts = postRedisRepository.getPosts(postIds);

        if (posts.size() < postIds.size()) {
            List<Long> foundedPostIds = posts.stream()
                    .map(PostRedisDto::id)
                    .toList();
            List<Long> missingPostIds = postIds.stream()
                    .filter(id -> !foundedPostIds.contains(id))
                    .toList();
            if (!missingPostIds.isEmpty()) {
                List<Post> postsFromDb = (List<Post>) postRepository.findAllById(missingPostIds);
                List<PostRedisDto> postRedisDto = postsFromDb.stream()
                        .map(post -> postRedisRepository.processGetRedisDtoFromDb(post.getId()))
                        .toList();
                postRedisRepository.savePosts(postRedisDto);
                return Stream.concat(posts.stream(), postRedisDto.stream())
                        .distinct()
                        .toList();
            }
        }
        return posts;
    }

    private List<UserRedisDto> getUserRedisDtosWithFallback(List<Long> userIds) {
        List<UserRedisDto> authors = userRedisRepository.getUserByIds(userIds);

        if (authors.size() < userIds.size()) {
            List<Long> foundedUserIds = authors.stream()
                    .map(UserRedisDto::id)
                    .toList();
            List<Long> missingUserIds = userIds.stream()
                    .filter(id -> !foundedUserIds.contains(id))
                    .toList();
            if (!missingUserIds.isEmpty()) {
                List<UserRedisDto> users = getUsersWithFallback(missingUserIds);
                return Stream.concat(authors.stream(), users.stream())
                        .distinct()
                        .toList();
            }
        }
        return authors;
    }

    private List<UserRedisDto> getUsersWithFallback(List<Long> missingUserIds) {
        try {
            List<UserViewDto> usersFromService = userClient.getUsersByIds(missingUserIds);

            List<UserRedisDto> userRedisDtos = usersFromService.stream()
                    .map(userMapper::toRedisDto)
                    .toList();
            userRedisRepository.saveUsers(usersFromService);
            return userRedisDtos;

        } catch (Exception e) {
            log.warn("Не удалось получить пользователей {} из user-service: {}",
                    missingUserIds, e.getMessage());
            return Collections.emptyList();
        }
    }
}