package faang.school.postservice.service.feed;

import faang.school.postservice.cache.AuthorCacheRepository;
import faang.school.postservice.cache.FeedCacheRepository;
import faang.school.postservice.cache.PostCacheRepository;
import faang.school.postservice.cache.model.author.AuthorCache;
import faang.school.postservice.cache.model.post.PostCache;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.author.AuthorMapper;
import faang.school.postservice.mapper.feed.FeedMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    @Value("${feed.posts-quantity-to-return}")
    private int postSize;

    private final FeedCacheRepository feedCacheRepository;
    private final UserContext userContext;
    private final PostRepository postRepository;
    private final AuthorCacheRepository authorCacheRepository;
    private final PostCacheRepository postCacheRepository;
    private final PostMapper postMapper;
    private final AuthorMapper authorMapper;
    private final UserService userService;
    private final FeedMapper feedMapper;

    @Override
    public List<FeedPostDto> getFeedList(Long lastPostId) {
        Long userId = userContext.getUserId();
        List<Long> postsIds = new ArrayList<>(feedCacheRepository.get(userId, lastPostId, postSize));

        if (postsIds.size() < postSize) {
            int remaining = postSize - postsIds.size();

            LocalDateTime lastPublishedAt = null;
            if (!postsIds.isEmpty()) {
                Long lastPostIdFromRedis = postsIds.get(postsIds.size() - 1);
                PostCache lastPostCache = postCacheRepository.get(lastPostIdFromRedis)
                        .orElse(null);
                if (lastPostCache != null) {
                    lastPublishedAt = lastPostCache.publishedAt();
                }
            }

            if (lastPublishedAt == null && lastPostId != null) {
                Post post = postRepository.findById(lastPostId).orElse(null);
                lastPublishedAt = post == null ? null : post.getPublishedAt();
            }

            List<Long> dbPostIds = postRepository.findFeedPosts(
                            userId,
                            lastPublishedAt,
                            PageRequest.of(0, remaining)
                    ).stream()
                    .map(Post::getId)
                    .collect(Collectors.toList());

            postsIds.addAll(dbPostIds);
        }

        return mapToFeedPostDto(postsIds);
    }

    private List<FeedPostDto> mapToFeedPostDto(List<Long> postsIds) {
        List<FeedPostDto> postDtoList = new ArrayList<>();

        for (Long postId : postsIds) {
            PostCache postCache = postCacheRepository.get(postId).orElseGet(() -> {
                Post post = postRepository.getByIdOrThrow(postId);
                return postMapper.toPostCache(post);
            });
            AuthorCache authorCache = authorCacheRepository.get(postCache.authorId()).orElseGet(() -> {
                UserDto userDto = userService.getUser(postCache.authorId());
                return authorMapper.toAuthorCache(userDto);
            });

            FeedPostDto feedPostDto = feedMapper.toFeedPostDto(postCache, authorMapper.toAuthorDto(authorCache));
            postDtoList.add(feedPostDto);
        }
        return postDtoList;
    }
}
