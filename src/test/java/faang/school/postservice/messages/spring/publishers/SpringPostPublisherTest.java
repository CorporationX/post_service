package faang.school.postservice.messages.spring.publishers;

import faang.school.postservice.dto.post.PostPublishedEventDto;
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
class SpringPostPublisherTest {
    @Mock
    private CacheService cacheService;

    @InjectMocks
    private SpringPostPublisher publisher;

    private static final Long POST_ID = 100L;
    private static final Long AUTHOR_ID = 200L;
    private static final Long PROJECT_ID = 300L;

    @Test
    void handlePostPublished_InvokeCacheService_shouldSavePostToCacheAfterCommit() {
        PostPublishedEventDto event = new PostPublishedEventDto(POST_ID, AUTHOR_ID, PROJECT_ID);

        publisher.handlePostPublished(event);

        verify(cacheService, times(1))
                .savePost(POST_ID, AUTHOR_ID, PROJECT_ID);

        verifyNoMoreInteractions(cacheService);
    }
}
