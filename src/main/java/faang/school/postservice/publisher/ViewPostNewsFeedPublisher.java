package faang.school.postservice.publisher;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.protobuf.generate.FeedEventProto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ViewPostNewsFeedPublisher implements EventPublisher<PostDto> {

    private final KafkaTemplate<byte[], byte[]> kafkaTemplate;

    @Value("${spring.kafka.topics.view_post.name}")
    private String topicName;

    @Override
    public void publish(PostDto postDto) {
        FeedEventProto.FeedEvent feedEvent = FeedEventProto.FeedEvent.newBuilder()
                .setPostId(postDto.getId())
                .setAuthorId(postDto.getAuthorId())
                .build();
        kafkaTemplate.send(topicName, feedEvent.toByteArray());
    }
}
