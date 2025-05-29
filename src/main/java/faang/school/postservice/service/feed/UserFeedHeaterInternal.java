package faang.school.postservice.service.feed;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.properties.feed.FeedCacheProperties;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFeedHeaterInternal {
    private final PostRepository postRepository;
    private final FeedCacheService feedCacheService;
    private final FeedManagementService feedManagementService;
    private final FeedCacheProperties feedCacheProperties;

    @Transactional
    public void heatUserInTransaction(UserDto user, List<Long> followeeIds) {
        List<Post> posts = postRepository.findLatestPostsByAuthors(
                followeeIds,
                feedCacheProperties.getMaxFeedSize()
        );

        posts.forEach(feedCacheService::cachePostAndAuthor);

        List<Long> postIds = posts.stream()
                .map(Post::getId)
                .toList();

        feedManagementService.rebuildFeed(user.id(), postIds);
    }
}
