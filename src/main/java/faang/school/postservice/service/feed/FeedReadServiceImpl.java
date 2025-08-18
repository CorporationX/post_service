package faang.school.postservice.service.feed;

import faang.school.postservice.client.FollowServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.feed.AuthorShortDto;
import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.cache.PostCachePort;
import faang.school.postservice.service.cache.UserCachePort;
import faang.school.postservice.service.cache.mapper.PostCacheMapper;
import faang.school.postservice.service.cache.model.PostCacheDto;
import faang.school.postservice.service.cache.model.UserCacheDto;
import faang.school.postservice.service.post.PostQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FeedReadServiceImpl implements FeedReadService {

    private final FeedCachePort feedCachePort;
    private final PostCachePort postCachePort;
    private final UserCachePort userCachePort;
    private final PostRepository postRepository;
    private final PostQueryService postQueryService;
    private final FollowServiceClient followServiceClient;
    private final UserServiceClient userServiceClient;

    @Override
    public List<FeedPostDto> getFeed(Long userId, Long afterPostId, int limit) {
        List<Long> ids = (afterPostId == null)
                ? feedCachePort.getTop(userId, limit)
                : feedCachePort.getAfter(userId, afterPostId, limit);

        List<FeedPostDto> result = new ArrayList<>();
        Set<Long> collected = new HashSet<>();

        for (Long postId : ids) {
            FeedPostDto dto = buildFromCaches(postId);
            if (dto == null) {
                dto = buildFromDb(postId, true);
            }
            if (dto != null) {
                result.add(dto);
                collected.add(postId);
            }
        }

        if (result.size() < limit) {
            int need = limit - result.size();
            List<Long> followees = safeList(followServiceClient.getFolloweeIds(userId));
            if (!followees.isEmpty()) {
                List<Post> extra = postQueryService.findLatestPublishedByAuthors(followees, need * 2);
                for (Post p : extra) {
                    if (collected.contains(p.getId())) continue;
                    FeedPostDto dto = buildFromDb(p.getId(), false);
                    if (dto != null) {
                        result.add(dto);
                        collected.add(p.getId());
                        if (result.size() >= limit) break;
                    }
                }
            }
        }

        return result;
    }

    private FeedPostDto buildFromCaches(Long postId) {
        PostCacheDto post = postCachePort.get(postId);
        if (post == null) return null;
        UserCacheDto author = userCachePort.get(post.authorId());
        AuthorShortDto authorDto;
        if (author == null) {
            try {
                var user = userServiceClient.getUser(post.authorId());
                if (user != null && user.id() != null) {
                    author = new UserCacheDto(user.id(), user.username(), user.email());
                    userCachePort.put(author);
                }
            } catch (Exception ignored) {}
        }
        authorDto = (author == null) ? new AuthorShortDto(post.authorId(), null, null)
                                     : new AuthorShortDto(author.id(), author.username(), author.email());
        return new FeedPostDto(
                post.id(),
                post.content(),
                post.projectId(),
                post.resourceKeys(),
                post.publishedAt(),
                authorDto
        );
    }

    private FeedPostDto buildFromDb(Long postId, boolean cacheIt) {
        return postRepository.findById(postId)
                .filter(p -> p.isPublished() && !p.isDeleted())
                .map(p -> {
                    if (cacheIt) {
                        postCachePort.put(PostCacheMapper.fromEntity(p));
                    }
                    AuthorShortDto authorDto = null;
                    try {
                        var user = userServiceClient.getUser(p.getAuthorId());
                        if (user != null && user.id() != null) {
                            var u = new UserCacheDto(user.id(), user.username(), user.email());
                            userCachePort.put(u);
                            authorDto = new AuthorShortDto(u.id(), u.username(), u.email());
                        }
                    } catch (Exception e) {
                        authorDto = new AuthorShortDto(p.getAuthorId(), null, null);
                    }
                    return new FeedPostDto(
                            p.getId(),
                            p.getContent(),
                            p.getProjectId(),
                            p.getResources() == null ? List.of() : p.getResources().stream().map(Resource::getKey).toList(),
                            p.getPublishedAt(),
                            authorDto
                    );
                })
                .orElse(null);
    }

    private static <T> List<T> safeList(List<T> list) {
        return list == null ? List.of() : list;
    }
}
