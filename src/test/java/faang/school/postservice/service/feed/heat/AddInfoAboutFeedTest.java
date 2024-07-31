package faang.school.postservice.service.feed.heat;

import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.cache.RedisCacheService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AddInfoAboutFeedTest {

    @InjectMocks
    private AddInfoAboutFeed addInfoAboutFeed;

    @Mock
    private RedisCacheService redisCacheService;

    @Mock
    private PostRepository postRepository;

    @Test
    public void testAddInfoToRedis() {
        Long userId = 1L;
        Long postId = 1L;

        Timestamp updatedTime = new Timestamp(System.currentTimeMillis());

        when(postRepository.getUpdatedTime(postId)).thenReturn(updatedTime);

        addInfoAboutFeed.addInfoToRedis(userId, postId);

        verify(postRepository, times(1)).getUpdatedTime(postId);
        verify(redisCacheService, times(1)).addPostToUserFeed(postId, userId, updatedTime.getTime());
    }
}