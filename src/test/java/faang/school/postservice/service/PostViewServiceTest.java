package faang.school.postservice.service;

import faang.school.postservice.dto.event.PostViewEvent;
import faang.school.postservice.producer.KafkaPostViewProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostViewServiceTest {

    @Mock
    PostRepository postRepository;

    @Mock
    PostValidator postValidator;

    @Mock
    KafkaPostViewProducer kafkaPostViewProducer;

    @InjectMocks
    PostViewService postViewService;

    long postId;
    long userId;

    @BeforeEach
    void setUp() {
        postId = 1;
        userId = 2;
    }

    @Test
    @DisplayName("Successfully handle post view and send event")
    void handlePostView() {
        postViewService.handlePostView(postId, userId);

        verify(postValidator).validatePostExistence(postId);
        verify(postRepository).incrementViewCount(postId);
        verify(kafkaPostViewProducer).sendMessage(any(PostViewEvent.class));
    }
}