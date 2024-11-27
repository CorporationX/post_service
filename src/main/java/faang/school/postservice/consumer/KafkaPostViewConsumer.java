package faang.school.postservice.consumer;

import com.google.protobuf.InvalidProtocolBufferException;
import faang.school.postservice.dto.post.PostViewEvent;
import faang.school.postservice.mapper.PostViewEventMapper;
import faang.school.postservice.protobuf.generate.PostViewEventProto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostViewConsumer implements KafkaConsumer<byte[]> {
    private final FeedService feedService;
    private final PostViewEventMapper postViewEventMapper;

    @Override
    @KafkaListener(topics = {"${spring.kafka.topics.post_views.name}"}, groupId = "first")
    public void processEvent(byte[] message) throws InvalidProtocolBufferException {
        PostViewEvent postViewEvent = postViewEventMapper.toEvent(
                PostViewEventProto.PostViewEvent.parseFrom(message)
        );

        feedService.addNewView(postViewEvent);
    }
}
