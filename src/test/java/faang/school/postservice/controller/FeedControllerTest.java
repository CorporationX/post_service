package faang.school.postservice.controller;

import faang.school.postservice.model.dto.redis.cache.RedisPostDto;
import faang.school.postservice.service.FeedService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedControllerTest {

    @InjectMocks
    private FeedController feedController;

    @Mock
    private FeedService feedService;

    @Test
    void testGetNewsFeed() {
        Long userId = 1L;
        List<RedisPostDto> expectedPosts = Arrays.asList(new RedisPostDto(), new RedisPostDto());

        when(feedService.getNewsFeed(eq(userId), anyInt(), anyInt())).thenReturn(expectedPosts);

        ResponseEntity<List<RedisPostDto>> response = feedController.getNewsFeed(userId, 0, 20);

        verify(feedService, times(1)).getNewsFeed(eq(userId), eq(0), eq(20));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedPosts.size(), response.getBody().size());
    }

    private void assertEquals(Object expected, Object actual) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
    }

    private void assertNotNull(Object object) {
        org.junit.jupiter.api.Assertions.assertNotNull(object);
    }
}
