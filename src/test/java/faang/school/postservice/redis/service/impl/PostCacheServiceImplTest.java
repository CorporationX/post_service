package faang.school.postservice.redis.service.impl;

import faang.school.postservice.model.dto.LikeDto;
import faang.school.postservice.model.event.kafka.CommentEventKafka;
import faang.school.postservice.model.event.kafka.PostEventKafka;
import faang.school.postservice.redis.model.dto.CommentRedisDto;
import faang.school.postservice.redis.model.entity.PostCache;
import faang.school.postservice.redis.repository.PostCacheRedisRepository;
import faang.school.postservice.redis.service.FeedCacheService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostCacheServiceImplTest {

    @Mock
    private PostCacheRedisRepository postCacheRedisRepository;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    RLock lock;

    @Mock
    FeedCacheService feedCacheService;

    @InjectMocks
    private PostCacheServiceImpl postCacheService;

    @Captor
    ArgumentCaptor<PostCache> captor = ArgumentCaptor.forClass(PostCache.class);

    private CommentEventKafka commentEventKafka;

    @BeforeEach
    public void setUp() {
        commentEventKafka = new CommentEventKafka();
        commentEventKafka.setPostId(1L);
        commentEventKafka.setCreatedAt(LocalDateTime.now());
        commentEventKafka.setCommentId(11L);
    }

    @Test
    public void testUpdatePostComments_Success() {
        CommentRedisDto commentRedisDto1 = new CommentRedisDto();
        commentRedisDto1.setPostId(1L);
        commentRedisDto1.setCreatedAt(LocalDateTime.now());
        CommentRedisDto commentRedisDto2 = new CommentRedisDto();
        commentRedisDto2.setPostId(1L);
        commentRedisDto2.setCreatedAt(LocalDateTime.now().plusMinutes(1));

        TreeSet<CommentRedisDto> comments = new TreeSet<>();
        comments.add(commentRedisDto1);
        comments.add(commentRedisDto2);

        LikeDto likeDto = LikeDto.builder()
                .userId(108L)
                .postId(42L)
                .build();

        PostCache postCache = new PostCache(1L, "some content", 5L,
                0, List.of(likeDto), 0, comments, LocalDateTime.now());

        when(postCacheRedisRepository.findById(commentEventKafka.getPostId())).thenReturn(Optional.of(postCache));
        when(redissonClient.getLock(anyString())).thenReturn(lock);

        postCacheService.updatePostComments(commentEventKafka);

        verify(postCacheRedisRepository).save(captor.capture());
        verify(postCacheRedisRepository, times(1)).save(captor.capture());
    }

    @Test
    public void testUpdatePostComments_CantFindPost() {
        NoSuchElementException exception = Assertions.assertThrows(NoSuchElementException.class, () -> {
            postCacheService.updatePostComments(commentEventKafka);
        });

        Assertions.assertEquals("Can't find post in redis with id: " + commentEventKafka.getPostId(),
                exception.getMessage());
    }

    @Test
    public void testUpdateFeedsInCache() {
        Long postId = 1L;
        PostEventKafka event = PostEventKafka.builder()
                .postId(postId)
                .followerIds(List.of(2L, 3L, 4L)).build();
        when(feedCacheService.getAndSaveFeed(anyLong(), eq(postId))).thenReturn(CompletableFuture.completedFuture(null));

        postCacheService.updateFeedsInCache(event);

        verify(feedCacheService, times(3)).getAndSaveFeed(anyLong(), eq(postId));
    }
}