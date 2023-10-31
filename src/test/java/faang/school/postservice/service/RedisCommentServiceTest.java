package faang.school.postservice.service;

import faang.school.postservice.dto.redis.CommentEventDto;
import faang.school.postservice.service.redis.CommentEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//@ExtendWith(MockitoExtension.class)
//class RedisCommentServiceTest {
//
//    @Mock
//    private CommentEventPublisher publisher;
//    @InjectMocks
//    private CommentService service;
//
//    @Test
//    void testCreate() {
//        service.create();
//        verify(publisher).publish(any(CommentEventDto.class));
//    }
//}