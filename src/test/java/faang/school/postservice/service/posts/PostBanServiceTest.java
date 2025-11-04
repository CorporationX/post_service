package faang.school.postservice.service.posts;

import faang.school.postservice.messages.redis.publishers.Publisher;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.listener.ChannelTopic;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class PostBanServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private Publisher publisher;

    @Mock
    private ChannelTopic userBanTopic;

    @Captor
    private ArgumentCaptor<List<Long>> captor;

    private PostBanService postBanService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        postBanService = new PostBanService(postRepository, publisher, userBanTopic);
        setField(postBanService, "banThreshold", 3);
        setField(postBanService, "batchSize", 2);
    }

    @Test
    void testPublishBanCandidatesSinglePage() {
        List<Long> users = List.of(1L, 2L);
        when(postRepository.findUsersToBanForPosts(3, PageRequest.of(0, 2))).thenReturn(users);
        when(postRepository.findUsersToBanForPosts(3, PageRequest.of(1, 2))).thenReturn(List.of());

        postBanService.publishBanCandidates();

        verify(publisher).publish(userBanTopic, users);
        verify(postRepository, times(2)).findUsersToBanForPosts(anyInt(), any());
    }

    @Test
    void testPublishBanCandidatesMultiplePages() {
        List<Long> firstPage = List.of(1L, 2L);
        List<Long> secondPage = List.of(3L);
        when(postRepository.findUsersToBanForPosts(3, PageRequest.of(0, 2))).thenReturn(firstPage);
        when(postRepository.findUsersToBanForPosts(3, PageRequest.of(1, 2))).thenReturn(secondPage);
        when(postRepository.findUsersToBanForPosts(3, PageRequest.of(2, 2))).thenReturn(List.of());

        postBanService.publishBanCandidates();

        verify(publisher).publish(userBanTopic, firstPage);
        verify(publisher).publish(userBanTopic, secondPage);
    }

    @Test
    void testPublishBanCandidatesEmpty() {
        when(postRepository.findUsersToBanForPosts(3, PageRequest.of(0, 2))).thenReturn(List.of());

        postBanService.publishBanCandidates();

        verify(publisher, never()).publish(any(), any());
    }

    @Test
    void testPublishBanCandidatesWithException() {
        List<Long> users = List.of(1L);
        when(postRepository.findUsersToBanForPosts(3, PageRequest.of(0, 2))).thenReturn(users);
        when(postRepository.findUsersToBanForPosts(3, PageRequest.of(1, 2))).thenReturn(List.of());
        doThrow(new RuntimeException("fail")).when(publisher).publish(userBanTopic, users);

        postBanService.publishBanCandidates();

        verify(publisher).publish(userBanTopic, users);
        verify(postRepository, times(2)).findUsersToBanForPosts(anyInt(), any());
    }
}
