package faang.school.postservice.redis.cache.service.comment_post;

import faang.school.postservice.redis.cache.entity.CommentCache;
import faang.school.postservice.redis.cache.entity.PostCache;
import faang.school.postservice.redis.cache.repository.PostCacheRepository;
import faang.school.postservice.redis.cache.service.RedisOperations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentPostCacheServiceImpl implements CommentPostCacheService {

    @Value("${spring.data.redis.cache.settings.max-post-comments-size}")
    private int maxPostCommentsSize;
    private final PostCacheRepository postCacheRepository;
    private final RedisOperations redisOperations;

    @Override
    public void tryDeleteCommentFromPost(CommentCache comment) {

        getPostAndPerform(comment, (post) -> {

            NavigableSet<CommentCache> comments = post.getComments();

            if (comments != null) {
                comments.remove(comment);
            }

            redisOperations.updateOrSave(postCacheRepository, post, post.getId());

            log.info("Removed comment with id={} from post cache: {}", comment.getId(), post);
        });
    }

    @Override
    public void tryAddCommentToPost(CommentCache comment) {

        getPostAndPerform(comment, (post) -> {

            NavigableSet<CommentCache> comments = post.getComments();

            if (comments == null) {
                comments = new TreeSet<>(Comparator.comparing(CommentCache::getCreatedAt));
                comments.add(comment);
                post.setComments(comments);
            } else {
                comments.add(comment);
                while (comments.size() > maxPostCommentsSize) {
                    comments.remove(comments.last());
                }
            }

            redisOperations.updateOrSave(postCacheRepository, post, post.getId());

            log.info("Added comment with id={} to post cache: {}", comment.getId(), post);
        });
    }

    private void getPostAndPerform(CommentCache comment, Consumer<PostCache> consumer) {

        redisOperations.findById(postCacheRepository, comment.getPostId()).ifPresentOrElse(
                consumer,
                () -> log.warn("Post with id={} not found", comment.getPostId())
        );
    }
}
