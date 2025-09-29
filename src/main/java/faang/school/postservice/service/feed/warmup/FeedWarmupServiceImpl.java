package faang.school.postservice.service.feed.warmup;

import faang.school.postservice.cache.author.AuthorCache;
import faang.school.postservice.cache.feed.FeedCache;
import faang.school.postservice.cache.post.PostCache;
import faang.school.postservice.dto.cache.PostCacheDto;
import faang.school.postservice.dto.user.feed.HeatUserTask;
import faang.school.postservice.mapper.feed.FeedMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.follow.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedWarmupServiceImpl implements FeedWarmupService {

    private final FollowService followService;
    private final PostRepository postRepository;
    private final FeedCache feedCache;
    private final PostCache postCache;
    private final AuthorCache authorCache;
    private final FeedMapper feedMapper;

    @Override
    public void warmUpUser(HeatUserTask task) {
        long userId = task.userId();
        int limit = task.limit();

        List<Long> followingAuthorIds = followService.getAllFollowingAuthorIds(userId);
        if (followingAuthorIds == null || followingAuthorIds.isEmpty()) {
            return;
        }

        List<Post> recentPosts = postRepository.findRecentByAuthors(followingAuthorIds, limit);
        if (recentPosts == null || recentPosts.isEmpty()) {
            return;
        }

        List<Long> postIds = new ArrayList<>(recentPosts.size());
        List<Instant> publishedInstants = new ArrayList<>(recentPosts.size());
        for (Post post : recentPosts) {
            postIds.add(post.getId());
            Instant publishedAtInstant = (post.getPublishedAt() != null)
                    ? post.getPublishedAt().toInstant(ZoneOffset.UTC)
                    : Instant.now();
            publishedInstants.add(publishedAtInstant);
        }
        feedCache.addAllPostsForUser(userId, postIds, publishedInstants);

        List<PostCacheDto> postCacheEntries = feedMapper.toPostCacheEntryList(recentPosts);
        postCache.putAll(postCacheEntries);

        Set<Long> uniqueAuthorIds = new HashSet<>();
        for (Post post : recentPosts) {
            Long authorId = post.getAuthorId();
            if (authorId != null) {
                uniqueAuthorIds.add(authorId);
            }
        }
        if (!uniqueAuthorIds.isEmpty()) {
            authorCache.preloadAll(new ArrayList<>(uniqueAuthorIds));
        }
    }
}