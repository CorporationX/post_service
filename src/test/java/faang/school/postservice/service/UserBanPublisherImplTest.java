package faang.school.postservice.service;

import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserBanPublisherImplTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RedisTemplate<String, Object> template;
    @Mock
    private ChannelTopic userBanTopic;
    @InjectMocks
    UserBanPublisherImpl userBanPublisher;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(userBanPublisher, "BATCH_SIZE", 1);
    }

    @Test
    public void banUserComment_listIsEmpty_shouldNotBeSend() {
        Page<Long> authorIds = Page.empty();
        when(commentRepository.findAllBanUser(PageRequest.of(0, 1))).thenReturn(authorIds);

        userBanPublisher.banUserComment();

        verify(template, times(0)).convertAndSend(any(String.class), any(String.class));
    }

    @Test
    public void banUserComment_serializeAndSendToTopic_shouldPublishedTopic() {
        when(commentRepository.findAllBanUser(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(10L), PageRequest.of(0, 1), 5))
                .thenReturn(new PageImpl<>(List.of(10L), PageRequest.of(1, 1), 5))
                .thenReturn(new PageImpl<>(List.of(10L), PageRequest.of(2, 1), 5))
                .thenReturn(new PageImpl<>(List.of(10L), PageRequest.of(3, 1), 5))
                .thenReturn(new PageImpl<>(List.of(10L), PageRequest.of(4, 1), 5))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(5, 1), 5));

        when(userBanTopic.getTopic()).thenReturn("user-ban-topic");

        System.out.println("Before calling banUserComment");

        userBanPublisher.banUserComment();

        System.out.println("After calling banUserComment");

        verify(template, times(1)).convertAndSend(eq("user-ban-topic"), any(String.class));
        verify(template).convertAndSend(eq("user-ban-topic"), eq("[10]"));
    }
}
