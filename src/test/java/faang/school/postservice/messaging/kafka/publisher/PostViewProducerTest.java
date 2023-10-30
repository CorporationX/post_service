package faang.school.postservice.messaging.kafka.publisher;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.corrector.external_service.TextGearsAPIService;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.messaging.kafka.events.PostViewEvent;
import faang.school.postservice.messaging.publishing.PostProducer;
import faang.school.postservice.messaging.kafka.publishing.PostViewProducer;
import faang.school.postservice.model.Post;
import faang.school.postservice.moderation.ModerationDictionary;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.s3.PostImageService;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostViewProducerTest {
    private final String topic = "post_view_event_channel";

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private PostMapper postMapper;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private TextGearsAPIService textGearsAPIService;

    @Mock
    private PostImageService postImageService;

    @Mock
    private ModerationDictionary moderationDictionary;

    @Mock
    private PostProducer postProducer;

    @Mock
    private ExecutorService executorService;

    @InjectMocks
    private PostViewProducer postViewProducer;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void init() {
        postService = new PostService(
                postRepository,
                postMapper,
                userServiceClient,
                projectServiceClient,
                textGearsAPIService,
                postImageService,
                moderationDictionary,
                postProducer,
                executorService,
                postViewProducer,
                userContext);
    }

    @Test
    public void shouldPublishPostViewEvent() {
        PostViewEvent event = PostViewEvent.builder()
                .userId(1L)
                .postId(12L)
                .build();
        postViewProducer.setTopic(topic);
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);

        when(kafkaTemplate.send(any(String.class), any(Object.class)))
                .thenAnswer(invocation -> future);
        postViewProducer.publish(event);

        verify(kafkaTemplate, times(1))
                .send(topic, event);
    }

    @Test
    public void shouldPublishEventWhenCallGetPostByIdMethod() {
        long postId = 1L;
        long userId = 2L;
        Post post = Post.builder()
                .id(postId)
                .deleted(false)
                .published(true)
                .build();
        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post));
        when(userContext.getUserId()).thenReturn(userId);

        PostViewEvent event = PostViewEvent.builder()
                .userId(userId)
                .postId(postId)
                .build();
        postViewProducer.setTopic(topic);
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);

        when(kafkaTemplate.send(any(String.class), any(Object.class)))
                .thenAnswer(invocation -> future);

        postService.getPostById(postId);

        verify(kafkaTemplate, times(1))
                .send(topic, event);
    }
}
