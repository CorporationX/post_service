package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.PostFeedDto;
import faang.school.postservice.mapper.FeedMapper;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.FeedRedisRepository;
import faang.school.postservice.repository.redis.PostRedisRepository;
import faang.school.postservice.repository.redis.UserRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static faang.school.postservice.data.FeedServiceImplTestData.USER_ID;
import static faang.school.postservice.data.FeedServiceImplTestData.getAuthors;
import static faang.school.postservice.data.FeedServiceImplTestData.getFeedDtos;
import static faang.school.postservice.data.FeedServiceImplTestData.getPostIdsFromDb;
import static faang.school.postservice.data.FeedServiceImplTestData.getPostIdsFromRedis;
import static faang.school.postservice.data.FeedServiceImplTestData.getPosts;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тестирование сервиса {@link FeedServiceImpl}
 *
 * @author Linempy
 * @since 02.10.2025
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование FeedServiceImpl")
public class FeedServiceImplTest {

    @Mock
    private UserContext userContext;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PostRepository postRepository;

    @Mock
    private FeedRedisRepository feedRedisRepository;

    @Mock
    private PostRedisRepository postRedisRepository;

    @Mock
    private UserRedisRepository userRedisRepository;

    @Mock
    private FeedMapper feedMapper;

    @InjectMocks
    private FeedServiceImpl feedService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(feedService, "feedSize", 10);

        doReturn(getPosts()).when(postRedisRepository).getPosts(anyList());
        doReturn(getAuthors()).when(userRedisRepository).getUserByIds(anyList());
        doReturn(getFeedDtos()).when(feedMapper).toFeedDtos(any(), any());
    }

    @Test
    @DisplayName("Должен вернуть ленту из Redis, когда там есть данные")
    void getFeedWhenRedisHasDataShouldReturnFeedFromRedis() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(feedRedisRepository.getFeed(null, USER_ID, 10))
                .thenReturn(getPostIdsFromRedis());

        List<PostFeedDto> result = feedService.getFeed(null);

        assertThat(result).isEqualTo(getFeedDtos());
        verify(postRepository).getFirstFeedOfUser(any(), anyInt());
        verify(feedRedisRepository).getFeed(null, USER_ID, 10);
    }

    @Test
    @DisplayName("Должен переключиться на БД, когда Redis пустой")
    void getFeedWhenRedisEmptyShouldFallbackDatabase() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(feedRedisRepository.getFeed(null, USER_ID, 10))
                .thenReturn(List.of());
        when(postRepository.getFirstFeedOfUser(USER_ID, 10))
                .thenReturn(getPostIdsFromDb());

        List<PostFeedDto> result = feedService.getFeed(null);

        assertThat(result).isEqualTo(getFeedDtos());
        verify(postRepository).getFirstFeedOfUser(USER_ID, 10);
        verify(feedRedisRepository).getFeed(null, USER_ID, 10);
    }
}