package faang.school.postservice.service.hashService;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.hash.PostHash;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.redis.PostRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EnableRetry
public class PostHashService {

    private final PostRedisRepository postRedisRepository;
    private final CommentMapper commentMapper;
    private final LikeMapper likeMapper;

    @Value("${spring.data.redis.time_to_live}")
    private long timeToLive;

    @Value(value = "${comments.max_count}")
    private int maxCount;

    @Async("executor")
    @Retryable(retryFor = OptimisticLockingFailureException.class,
            maxAttemptsExpression = "${spring.data.redis.retry_max_attempts}")
    public void save(Post post) {
        PostHash postHash = PostHash.builder()
                .id(post.getId())
                .content(post.getContent())
                .authorId(post.getAuthorId())
                .projectId(post.getProjectId())
                .publishedAt(post.getPublishedAt())
                .updatedAt(post.getUpdatedAt())
                .likes(getLikeDto(post.getLikes()))
                .timeToLive(timeToLive)
                .build();
        post.getComments().forEach(
                comment -> postHash.addComment(commentMapper.toDto(comment)));
        postRedisRepository.saveInRedis(postHash);
    }

    private LinkedBlockingDeque<CommentDto> getComments(Post post) {
        return post.getComments()
                .stream()
                .map(commentMapper::toDto)
                .sorted(Comparator.comparing(CommentDto::getCreatedAt).reversed())
                .limit(maxCount)
                .collect(LinkedBlockingDeque::new,
                        LinkedBlockingDeque::addFirst,
                        LinkedBlockingDeque::addAll);
    }

    private ConcurrentLinkedDeque<LikeDto> getLikeDto(List<Like> likes) {
        return likes.stream()
                .map(likeMapper::toDto)
                .collect(Collectors.toCollection(ConcurrentLinkedDeque::new));
    }
}
