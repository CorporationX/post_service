package faang.school.postservice.kafka.consumer;

import faang.school.postservice.cache.model.CacheableComment;
import faang.school.postservice.cache.model.CacheableUser;
import faang.school.postservice.cache.service.CacheablePostService;
import faang.school.postservice.kafka.event.comment.CommentAddedEvent;
import faang.school.postservice.mapper.CommentMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentEventConsumerTest {
    @InjectMocks
    private CommentEventConsumer commentEventConsumer;
    @Mock
    private CacheablePostService cacheablePostService;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void testCommentAddedEvent() {
        CacheableComment cacheableComment = CacheableComment.builder()
                .id(2L)
                .content("content")
                .author(CacheableUser.builder().id(12L).build())
                .postId(24L)
                .build();
        CommentAddedEvent event = CommentAddedEvent.builder()
                .commentId(cacheableComment.getId())
                .content(cacheableComment.getContent())
                .authorId(cacheableComment.getAuthor().getId())
                .postId(cacheableComment.getPostId())
                .build();
        when(commentMapper.toCacheable(event)).thenReturn(cacheableComment);

        commentEventConsumer.consume(event, acknowledgment);

        verify(cacheablePostService, times(1)).addCommentConcurrent(cacheableComment);
        verify(acknowledgment).acknowledge();
    }
}