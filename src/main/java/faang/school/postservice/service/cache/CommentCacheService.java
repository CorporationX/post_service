package faang.school.postservice.service.cache;

import faang.school.postservice.config.NewsFeedProperties;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.repository.cache.SortedSetCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommentCacheService implements SingleCacheService<Long, CommentDto>,
        MultiGetCacheService<Long, CommentDto>,
        MultiSaveCacheService<CommentDto> {

    private final SortedSetCacheRepository<CommentDto> sortedSetCacheRepository;
    private final NewsFeedProperties newsFeedProperties;

    @Override
    public void save(Long postId, CommentDto comment) {
        String postIdKey = createKey(postId);
        Runnable runnable = () -> {
            sortedSetCacheRepository.put(postIdKey, comment, System.currentTimeMillis());

            if (sortedSetCacheRepository.size(postIdKey) >= newsFeedProperties.getLimitCommentsOnPost()) {
                sortedSetCacheRepository.popMin(postIdKey);
            }
        };

        sortedSetCacheRepository.executeInOptimisticLock(runnable, postIdKey);
    }

    @Override
    public void saveAll(List<CommentDto> posts) {
        for (CommentDto comment : posts) {
            save(comment.getId(), comment);
        }
    }

    @Override
    public CommentDto get(Long postId) {
        List<CommentDto> comments = getAll(postId);
        return comments.isEmpty() ? null : comments.get(0);
    }

    @Override
    public List<CommentDto> getAll(Long postId) {
        Set<CommentDto> comments = sortedSetCacheRepository.get(createKey(postId));
        return new ArrayList<>(comments);
    }

    private String createKey(Long postId) {
        return postId + "::comments_for_news_feed";
    }
}
