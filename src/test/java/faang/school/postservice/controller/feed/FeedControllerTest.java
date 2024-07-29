package faang.school.postservice.controller.feed;

import faang.school.postservice.service.feed.FeedService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FeedControllerTest {

    @InjectMocks
    private FeedController feedController;

    @Mock
    private FeedService feedService;

    @Test
    public void testCorrectWorkGetFeedWithNullInput() {
        String afterPostId = null;

        assertDoesNotThrow(() -> feedController.getFeed(afterPostId));
        verify(feedService).getFeed(afterPostId);
    }

    @Test
    public void testCorrectWorkGetFeedWithNotNullInput() {
        String afterPostId = "123";

        assertDoesNotThrow(() -> feedController.getFeed(afterPostId));
        verify(feedService).getFeed(afterPostId);
    }
}
