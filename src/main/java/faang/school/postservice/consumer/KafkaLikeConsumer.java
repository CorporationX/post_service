package faang.school.postservice.consumer;

import com.google.protobuf.InvalidProtocolBufferException;
import faang.school.postservice.dto.like.LikePostEvent;
import faang.school.postservice.mapper.LikePostEventMapper;
import faang.school.postservice.protobuf.generate.LikePostEventProto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer implements KafkaConsumer<byte[]> {
    private final FeedService feedService;
    private final LikePostEventMapper likePostEventMapper;

    @Override
    @KafkaListener(topics = {"${spring.kafka.topics.likes.name}"}, groupId = "first")
    public void processEvent(byte[] message) throws InvalidProtocolBufferException {
        LikePostEvent event = likePostEventMapper.toEvent(
                LikePostEventProto.LikePostEvent.parseFrom(message)
        );

        feedService.addNewLike(event);
    }
}
