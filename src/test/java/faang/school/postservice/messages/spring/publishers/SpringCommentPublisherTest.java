package faang.school.postservice.messages.spring.publishers;

import faang.school.postservice.dto.comment.ModelEventDto;
import faang.school.postservice.service.cache.CacheService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.event.RecordApplicationEvents;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;


@ExtendWith(MockitoExtension.class)
@RecordApplicationEvents
class SpringCommentPublisherTest {
    @Mock
    private CacheService cacheService;

    @InjectMocks
    private SpringCommentPublisher publisher;

    private static final Long AUTHOR_ID = 42L;
    private static final Long COMMENT_ID = 100500L;

    @Test
    void handleCommentCreated_InvokeCacheService_shouldSaveAuthorToCacheAfterCommit() {
        ModelEventDto event = new ModelEventDto(AUTHOR_ID, COMMENT_ID);

        publisher.handleCommentCreated(event);

        verify(cacheService, times(1))
                .saveAuthor(AUTHOR_ID, COMMENT_ID);

        verifyNoMoreInteractions(cacheService);
    }
}