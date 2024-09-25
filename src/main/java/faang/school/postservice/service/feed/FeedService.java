package faang.school.postservice.service.feed;

import faang.school.postservice.cache.UserCache;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.cache.FeedCache;
import faang.school.postservice.cache.PostCache;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {

    private final FeedCache feedCache;
    private final PostCache postCache;
    private final UserCache userCache;
    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    public void saveFeed(Long userid, Long postId) {
        feedCache.save(userid, postId);
    }

    public FeedDto getFeed(Long postId) {
        long userId = userContext.getUserId();
        List<PostDto> posts;
        List<UserDto> users;

        Optional<Set<String>> postsId = feedCache.getByRange(userId, postId);

        if (postsId.isPresent()) {
            posts = getPosts(postsId.get().stream().toList());
            users = getUsers(posts.stream().map(PostDto::getAuthorId).distinct().toList());

        } else {
            users = userServiceClient.getUserFollows(userId);
            List<Long> userAuthorsIds = users.stream()
                    .map(UserDto::getId)
                    .distinct()
                    .toList();

            posts = postService.getByAuthorIds(userAuthorsIds, postId);

        }

        return prepareToFeed(posts, users);
    }

    public List<PostDto> getPosts(List<String> postIds) {
        List<Long> convertedIds = postIds.stream().map(Long::valueOf).toList();
        List<PostDto> posts = postCache.getByList(convertedIds);

        if (posts.isEmpty()) {
            posts = postService.getByPostsIds(convertedIds);
        }

        return posts;
    }

    public List<UserDto> getUsers(List<Long> usersIds) {
        List<UserDto> users = userCache.getByIdList(usersIds);

        if (users.isEmpty()) {
            users = userServiceClient.getUsersByIds(usersIds);
        }

        return users;
    }

    private FeedDto prepareToFeed(List<PostDto> posts, List<UserDto> users) {
        return FeedDto.builder()
                .posts(posts)
                .postAuthors(users)
                .build();
    }
}
