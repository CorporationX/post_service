package faang.school.postservice.app.listener;

import faang.school.postservice.kafka.producer.AuthorPostKafkaProducer;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.RedisPostDtoMapper;
import faang.school.postservice.model.dto.CommentDto;
import faang.school.postservice.model.dto.redis.cache.RedisCommentDto;
import faang.school.postservice.model.dto.redis.cache.RedisPostDto;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.model.event.application.PostsPublishCommittedEvent;
import faang.school.postservice.model.event.kafka.AuthorPostKafkaEvent;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.service.RedisPostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostPublishCommitedEventListenerTest {

    @Mock
    private RedisPostService redisPostService;

    @Mock
    private RedisPostDtoMapper redisPostDtoMapper;

    @Mock
    private PostMapper postMapper;

    @Mock
    private AuthorPostKafkaProducer authorPostKafkaProducer;

    @Mock
    private CommentService commentService;

    @Mock
    private LikeService likeService;

    @InjectMocks
    private PostPublishCommitedEventListener listener;

    @Test
    void testHandlePostsPublishCommittedEvent() {
        // Arrange
        Post post1 = new Post();
        post1.setId(1L);
        post1.setAuthorId(10L);
        post1.setContent("Post 1 content");
        post1.setCreatedAt(LocalDateTime.now());
        post1.setPublishedAt(LocalDateTime.now());
        post1.setViewCount(100);

        Post post2 = new Post();
        post2.setId(2L);
        post2.setAuthorId(20L);
        post2.setContent("Post 2 content");
        post2.setCreatedAt(LocalDateTime.now());
        post2.setPublishedAt(LocalDateTime.now());
        post2.setViewCount(200);

        List<Post> posts = List.of(post1, post2);
        PostsPublishCommittedEvent event = new PostsPublishCommittedEvent(posts);

        Map<Long, List<CommentDto>> top3CommentsForPosts = Map.of(
                1L, List.of(
                        createCommentDto(101L, "Comment 1", 11L, LocalDateTime.now()),
                        createCommentDto(102L, "Comment 2", 12L, LocalDateTime.now())
                ),
                2L, List.of(
                        createCommentDto(103L, "Comment 3", 21L, LocalDateTime.now())
                )
        );

        Map<Long, Integer> postIdCommentCountMap = Map.of(
                1L, 5,
                2L, 10
        );

        Map<Long, Integer> postIdLikeCountMap = Map.of(
                1L, 50,
                2L, 100
        );

        when(commentService.getTop3CommentsForPosts(anyList())).thenReturn(top3CommentsForPosts);
        when(commentService.getPostIdCommentCountMap(anyList())).thenReturn(postIdCommentCountMap);
        when(likeService.getPostIdLikeCountMap(anyList())).thenReturn(postIdLikeCountMap);

        // Act
        listener.handlePostsPublishCommittedEvent(event);

        // Assert
        // Проверяем вызов AuthorPostKafkaProducer
        verify(authorPostKafkaProducer, times(2)).sendEvent(any(AuthorPostKafkaEvent.class));
        ArgumentCaptor<AuthorPostKafkaEvent> kafkaEventCaptor = ArgumentCaptor.forClass(AuthorPostKafkaEvent.class);
        verify(authorPostKafkaProducer, times(2)).sendEvent(kafkaEventCaptor.capture());

        List<AuthorPostKafkaEvent> capturedKafkaEvents = kafkaEventCaptor.getAllValues();
        assertEquals(1L, capturedKafkaEvents.get(0).getPostId());
        assertEquals(10L, capturedKafkaEvents.get(0).getAuthorId());
        assertEquals(2L, capturedKafkaEvents.get(1).getPostId());
        assertEquals(20L, capturedKafkaEvents.get(1).getAuthorId());

        // Проверяем вызов RedisPostService
        verify(redisPostService, times(2)).savePostIfNotExists(any(RedisPostDto.class));
        ArgumentCaptor<RedisPostDto> redisPostCaptor = ArgumentCaptor.forClass(RedisPostDto.class);
        verify(redisPostService, times(2)).savePostIfNotExists(redisPostCaptor.capture());

        List<RedisPostDto> capturedRedisPosts = redisPostCaptor.getAllValues();
        assertEquals(1L, capturedRedisPosts.get(0).getPostId());
        assertEquals(10L, capturedRedisPosts.get(0).getAuthorId());
        assertEquals(2L, capturedRedisPosts.get(1).getPostId());
        assertEquals(20L, capturedRedisPosts.get(1).getAuthorId());
    }

    private CommentDto createCommentDto(Long id, String content, Long authorId, LocalDateTime createdAt) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(id);
        commentDto.setPostId(id); // Используем id поста для идентификации
        commentDto.setContent(content);
        commentDto.setAuthorId(authorId);
        commentDto.setCreatedAt(createdAt);
        return commentDto;
    }
}
