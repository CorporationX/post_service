package faang.school.postservice.messages.redis.publishers;


import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.util.List;

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
    private UserBanPublisherImpl userBanPublisher;


    @Test
    void banUserComment_whenUsersFound_shouldSendMessage() {
        List<Long> userIds = List.of(1L, 2L, 3L);

        when(commentRepository.findAllBanUser(0)).thenReturn(userIds);
        when(userBanTopic.getTopic()).thenReturn("user-ban-topic");

        userBanPublisher.banUserComment();

        verify(commentRepository).findAllBanUser(0);
        verify(template).convertAndSend("user-ban-topic", "[1,2,3]");
    }
}