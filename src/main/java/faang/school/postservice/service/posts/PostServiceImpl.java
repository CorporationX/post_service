package faang.school.postservice.service.posts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostCreatedEvent;
import faang.school.postservice.dto.post.RequestPostDto;
import faang.school.postservice.exception.OutboxSerializationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.outbox.entity.OutboxEvent;
import faang.school.postservice.outbox.entity.OutboxEventType;
import faang.school.postservice.outbox.entity.OutboxStatus;
import faang.school.postservice.model.Post;
import faang.school.postservice.outbox.repository.OutboxRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final UserContext userContext;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${spring.application.name}")
    private String serviceName;

    @Override
    @Transactional
    public PostDto create(RequestPostDto requestPostDto) {
        Post post = postMapper.toModel(requestPostDto);
        post.setAuthorId(userContext.getUserId());
        Post savedPost = postRepository.save(post);

        eventPublisher.publishEvent(new PostCreatedInternalEvent(savedPost));

        PostCreatedEvent event = PostCreatedEvent.builder()
                .id(savedPost.getId())
                .content(savedPost.getContent())
                .projectId(savedPost.getProjectId())
                .authorId(savedPost.getAuthorId())
                .createdAt(savedPost.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli())
                .build();

        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new OutboxSerializationException("Failed to serialize PostCreatedEvent for Outbox", e);
        }

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateId(savedPost.getId())
                .status(OutboxStatus.NEW)
                .eventType(OutboxEventType.POST_CREATED)
                .payload(payload)
                .sourceService(serviceName)
                .build();

        outboxRepository.save(outboxEvent);

        return postMapper.toDto(savedPost);
    }
}
