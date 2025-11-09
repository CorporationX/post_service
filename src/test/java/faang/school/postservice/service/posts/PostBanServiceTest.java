package faang.school.postservice.service.posts;

import faang.school.postservice.messages.redis.publishers.Publisher;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class PostBanServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private Publisher publisher;

    @Mock
    private ChannelTopic userBanTopic;

    @InjectMocks
    private PostBanService postBanService;

    private final int BAN_THRESHOLD = 3;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(postBanService, "banThreshold", BAN_THRESHOLD);
    }

    @Test
    void publishBanCandidates_WhenUsersFound_ShouldPublishUsers() {
        List<Long> expectedUsers = List.of(1L, 2L, 3L);
        when(postRepository.findUsersToBanForPosts(BAN_THRESHOLD)).thenReturn(expectedUsers);

        postBanService.publishBanCandidates();

        verify(publisher).publish(userBanTopic, expectedUsers);
    }

    @Test
    void publishBanCandidates_WhenNoUsersFound_ShouldNotPublish() {
        when(postRepository.findUsersToBanForPosts(BAN_THRESHOLD)).thenReturn(List.of());

        postBanService.publishBanCandidates();

        verify(publisher, never()).publish(userBanTopic, List.of());
    }

    @Test
    void publishBanCandidates_WhenPublishingFails_ShouldLogError() {
        List<Long> users = List.of(1L, 2L);
        RuntimeException exception = new RuntimeException("Redis connection failed");

        when(postRepository.findUsersToBanForPosts(BAN_THRESHOLD)).thenReturn(users);
        doThrow(exception).when(publisher).publish(userBanTopic, users);

        postBanService.publishBanCandidates();

        verify(publisher).publish(userBanTopic, users);
    }

    @Test
    void publishBanCandidates_WithSingleUser_ShouldPublishSuccessfully() {
        List<Long> singleUser = List.of(1L);
        when(postRepository.findUsersToBanForPosts(BAN_THRESHOLD)).thenReturn(singleUser);

        postBanService.publishBanCandidates();

        verify(publisher).publish(userBanTopic, singleUser);
    }

    @Test
    void publishBanCandidates_WithMultipleUsers_ShouldPublishAllUsers() {
        List<Long> multipleUsers = List.of(1L, 2L, 3L, 4L, 5L);
        when(postRepository.findUsersToBanForPosts(BAN_THRESHOLD)).thenReturn(multipleUsers);

        postBanService.publishBanCandidates();

        verify(publisher).publish(userBanTopic, multipleUsers);
    }
}
