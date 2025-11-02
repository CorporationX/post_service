package faang.school.postservice.service;

import faang.school.postservice.repository.CommentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

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
    public void setUp(){
        ReflectionTestUtils.setField(userBanPublisher, "BATCH_SIZE", 1);
    }

    @Test
    public void banUserComment_listIsEmpty_shouldNotBeSend() {

    }
}