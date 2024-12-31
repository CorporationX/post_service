package faang.school.postservice.event;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.kafka.producer.KafkaEventProducer;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventsBuilder {
    private final KafkaEventProducer kafkaEventProducer;
    private final UserServiceClient userServiceClient;

    public void buildAndSendPostFollowersEvent(PostDto postDto) {
        UserDto author = userServiceClient.getUser(postDto.getAuthorId());
        PostFollowersEventRecord event = PostFollowersEventRecord.builder()
                .authorId(postDto.getAuthorId())
                .followersIds(author.getFollowerIds())
                .publishedAt(postDto.getPublishedAt())
                .build();

        kafkaEventProducer.sendPostFollowersEvent(event);
    }

    public void buildAndSendPostViewEvent(PostDto postDto) {
        PostViewEventRecord event = PostViewEventRecord.builder()
                .postId(postDto.getId())
                .build();

        kafkaEventProducer.sendPostViewEvent(event);
    }

    public void buildAndSendCommentEventToKafka(CommentDto commentDto) {
        CommentEventRecord event = CommentEventRecord.builder()
                .commentDto(commentDto)
                .authorId(commentDto.getAuthorId())
                .postId(commentDto.getPostId())
                .content(commentDto.getContent())
                .build();

        kafkaEventProducer.sendCommentEvent(event);
    }
}