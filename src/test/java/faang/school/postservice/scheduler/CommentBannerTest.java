package faang.school.postservice.scheduler;

import faang.school.postservice.publis.publisher.UserBanMessagePublisher;
import faang.school.postservice.redis.publisher.BanAuthorPublisher;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentBannerTest {
    @InjectMocks
    private CommentBanner commentBanner;
    @Mock
    private CommentService commentService;
    @Mock
    private BanAuthorPublisher publisher;

    @Test
    public void testPublishAuthorIds() {
        List<Long> ids = List.of(1L, 2L);
        when(commentService.getAuthorIdsToBeBanned()).thenReturn(ids);

        commentBanner.retrieveAndPublishViolatingAuthorIds();

        verify(commentService, times(1)).getAuthorIdsToBeBanned();
        for (Long id : ids) {
            verify(publisher, times(1)).publish(id.toString());
        }
    }
}
