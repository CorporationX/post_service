package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.feed.PostFeedDto;
import faang.school.postservice.service.feed.FeedService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тестирование REST-контроллера {@link FeedController}.
 */
@ExtendWith(MockitoExtension.class)
class FeedControllerTest {

    @Mock
    private FeedService feedService;

    @InjectMocks
    private FeedController feedController;

    @Test
    void getFeedShouldReturnFirstPageWhenLastPostIdIsMissing() {
        List<PostFeedDto> expected = List.of();
        when(feedService.getFeed(null)).thenReturn(expected);

        ResponseEntity<List<PostFeedDto>> response = feedController.getFeed(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(feedService).getFeed(null);
    }

    @Test
    void getFeedShouldReturnNextPageWhenLastPostIdIsSpecified() {
        Long lastPostId = 10L;
        List<PostFeedDto> expected = List.of();
        when(feedService.getFeed(lastPostId)).thenReturn(expected);

        ResponseEntity<List<PostFeedDto>> response = feedController.getFeed(lastPostId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(feedService).getFeed(lastPostId);
    }
}
