//package faang.school.postservice.publisher;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import faang.school.postservice.dto.event.LikeEvent;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.listener.ChannelTopic;
//
//import java.time.LocalDateTime;
//
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class LikeEventPublisherTest {
//    @Mock
//    private RedisTemplate<String, Object> redisTemplate;
//
//    @Mock
//    private ChannelTopic channelTopic;
//
//    @Mock
//    private ObjectMapper objectMapper;
//
//    @InjectMocks
//    private LikeEventPublisher likeEventPublisher;
//    private LikeEvent likeEvent;
//    String message = "test message";
//
//    @BeforeEach
//    public void setUp() {
//        likeEvent = LikeEvent.builder()
//                .postId(1L)
//                .authorId(2L)
//                .userId(3L)
//                .eventAt(LocalDateTime.now())
//                .build();
//    }
//
//    @Test
//    @DisplayName("writeValueAsString")
//    public void testWriteValueAsString() throws Exception {
//        when(objectMapper.writeValueAsString(likeEvent)).thenReturn(message);
//
//        likeEventPublisher.publish(likeEvent);
//
//        verify(redisTemplate).convertAndSend(channelTopic.getTopic(), message);
//    }
//
//    @Test
//    @DisplayName("getTopic")
//    public void testGetTopic() throws Exception {
//        when(objectMapper.writeValueAsString(Mockito.any(LikeEvent.class)))
//                .thenReturn(message);
//        when(channelTopic.getTopic())
//                .thenReturn("test-topic");
//
//        likeEventPublisher.publish(likeEvent);
//
//        verify(objectMapper, times(1))
//                .writeValueAsString(Mockito.any(LikeEvent.class));
//        verify(redisTemplate, times(1))
//                .convertAndSend("test-topic", message);
//    }
//
//    @Test
//    @DisplayName("convertAndSend")
//    public void testConvertAndSend() throws Exception {
//        when(objectMapper.writeValueAsString(Mockito.any(LikeEvent.class)))
//                .thenReturn(message);
//        when(channelTopic.getTopic())
//                .thenReturn("test-topic");
//        when(redisTemplate.convertAndSend(anyString(), anyString()))
//                .thenReturn(anyLong());
//
//        likeEventPublisher.publish(likeEvent);
//
//        verify(objectMapper, times(1))
//                .writeValueAsString(Mockito.any(LikeEvent.class));
//        verify(redisTemplate, times(1))
//                .convertAndSend("test-topic", message);
//        verify(redisTemplate, times(1))
//                .convertAndSend(anyString(), anyString());
//    }
//}