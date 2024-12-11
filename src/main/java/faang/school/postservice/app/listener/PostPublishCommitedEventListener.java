package faang.school.postservice.app.listener;

import faang.school.postservice.kafka.producer.AuthorPostKafkaProducer;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.RedisPostDtoMapper;
import faang.school.postservice.model.dto.CommentDto;
import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.model.dto.redis.cache.RedisCommentDto;
import faang.school.postservice.model.dto.redis.cache.RedisPostDto;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.model.event.application.PostsPublishCommittedEvent;
import faang.school.postservice.model.event.kafka.AuthorPostKafkaEvent;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.service.RedisPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostPublishCommitedEventListener {
    private final RedisPostService redisPostService;
    private final RedisPostDtoMapper redisPostDtoMapper;
    private final PostMapper postMapper;
    private final AuthorPostKafkaProducer authorPostKafkaProducer;
    private final CommentService commentService;
    private final LikeService likeService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostsPublishCommittedEvent(PostsPublishCommittedEvent event) {
        List<Post> posts = event.getPosts();
        log.info("Processing committed posts: {}", posts.size());

        List<Long> postIds = posts.stream()
                .map(Post::getId)
                .toList();
        Map<Long, List<CommentDto>> top3CommentsForPosts = commentService.getTop3CommentsForPosts(postIds);
        Map<Long, Integer> postIdCommentCountMap = commentService.getPostIdCommentCountMap(postIds);
        Map<Long, Integer> postIdLikeCountMap = likeService.getPostIdLikeCountMap(postIds);
        Map<Long, Integer> postIdViewCountMap = posts.stream()
                .collect(Collectors.toMap(Post::getId, Post::getViewCount));

        posts.forEach(post -> {
            log.debug("Sending AuthorPublishedPostKafkaEvent for author with id = {} in Kafka for user-service",
                    post.getAuthorId());
            authorPostKafkaProducer.sendEvent(
                    new AuthorPostKafkaEvent(post.getId(), post.getAuthorId(), post.getPublishedAt()));
            log.debug("Saving post with id = {} in Redis if needed", post.getId());
            RedisPostDto redisPostDto = RedisPostDto.builder()
                    .postId(post.getId())
                    .authorId(post.getAuthorId())
                    .content(post.getContent())
                    .createdAt(post.getCreatedAt())
                    .commentCount(postIdCommentCountMap.get(post.getId()))
                    .likeCount(postIdLikeCountMap.get(post.getId()))
                    .recentComments(top3CommentsForPosts.get(post.getId()).stream()
                            .map(commentDto -> new RedisCommentDto(commentDto.getAuthorId(), commentDto.getContent()))
                            .toList())
                    .viewCount(postIdViewCountMap.get(post.getId()))
                    .build();
            redisPostService.savePostIfNotExists(redisPostDto);
        });
    }
}
