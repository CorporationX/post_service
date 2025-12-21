package faang.school.postservice.service.posts;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostCreatedEvent;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.RequestPostDto;
import faang.school.postservice.exception.OutboxSerializationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.outbox.entity.OutboxEvent;
import faang.school.postservice.outbox.entity.OutboxEventType;
import faang.school.postservice.outbox.entity.OutboxStatus;
import faang.school.postservice.outbox.repository.OutboxRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostMapper postMapper;
    @Mock
    private UserContext userContext;
    @Mock
    private OutboxRepository outboxRepository;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private PostServiceImpl postService;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepository.class);
        postMapper = mock(PostMapper.class);
        userContext = mock(UserContext.class);
        outboxRepository = mock(OutboxRepository.class);
        objectMapper = new ObjectMapper();
        postService = new PostServiceImpl(postMapper, postRepository, userContext, outboxRepository, objectMapper);
    }

    @Test
    void create_shouldSavePostAndOutboxEvent() throws JsonProcessingException {
        RequestPostDto requestDto = new RequestPostDto(
                "Some content",
                123L
        );
        Post postModel = new Post();
        postModel.setContent("content");
        postModel.setProjectId(123L);

        Post savedPost = new Post();
        savedPost.setId(1L);
        savedPost.setContent("content");
        savedPost.setProjectId(123L);
        savedPost.setAuthorId(42L);

        PostDto postDto = new PostDto(
                1L,
                "content",
                123L,
                42L,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(postMapper.toModel(requestDto)).thenReturn(postModel);
        when(userContext.getUserId()).thenReturn(42L);
        when(postRepository.save(postModel)).thenReturn(savedPost);
        when(postMapper.toDto(savedPost)).thenReturn(postDto);

        PostDto result = postService.create(requestDto);

        assertEquals(postDto, result);

        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxRepository).save(captor.capture());
        OutboxEvent capturedEvent = captor.getValue();

        assertEquals(savedPost.getId(), capturedEvent.getAggregateId());
        assertEquals(OutboxStatus.NEW, capturedEvent.getStatus());
        assertEquals(OutboxEventType.POST_CREATED, capturedEvent.getEventType());
        assertEquals("post-service", capturedEvent.getSourceService());

        String payload = capturedEvent.getPayload();
        assertTrue(payload.contains("\"id\":1"));
        assertTrue(payload.contains("\"content\":\"content\""));
        assertTrue(payload.contains("\"projectId\":123"));
        assertTrue(payload.contains("\"authorId\":42"));
    }

    @Test
    void create_shouldThrowOutboxSerializationException_whenJsonFails() throws JsonProcessingException {
        ObjectMapper badMapper = mock(ObjectMapper.class);
        postService = new PostServiceImpl(postMapper, postRepository, userContext, outboxRepository, badMapper);

        RequestPostDto requestDto = new RequestPostDto(
                "Some content",
                123L
        );
        Post postModel = new Post();
        Post savedPost = new Post();
        savedPost.setId(1L);
        savedPost.setContent("content");
        savedPost.setProjectId(123L);
        savedPost.setAuthorId(42L);

        when(postMapper.toModel(requestDto)).thenReturn(postModel);
        when(userContext.getUserId()).thenReturn(42L);
        when(postRepository.save(postModel)).thenReturn(savedPost);
        when(badMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

        assertThrows(OutboxSerializationException.class, () -> postService.create(requestDto));
    }
}