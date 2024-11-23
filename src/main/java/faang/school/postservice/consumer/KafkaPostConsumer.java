package faang.school.postservice.consumer;

import com.google.protobuf.InvalidProtocolBufferException;
import faang.school.postservice.dto.post.PostPublishedEvent;
import faang.school.postservice.mapper.post.PostPublishedEventMapper;
import faang.school.postservice.protobuf.generate.PostPublishedEventProto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostConsumer implements KafkaConsumer<byte[]> {
    private final PostPublishedEventMapper mapper;
    private final FeedService service;

    @Override
    @KafkaListener(topics = {"${spring.kafka.topics.posts.name}"}, groupId = "first")
    public void processEvent(byte[] message) throws InvalidProtocolBufferException {
        PostPublishedEventProto.PostPublishedEvent proto = PostPublishedEventProto
                .PostPublishedEvent.newBuilder().mergeFrom(message).build();
        PostPublishedEvent event = mapper.toEvent(proto);
        service.distributePostsToUsersFeeds(event);
    }
}
