package faang.school.postservice.event.kafka;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.kafka.producer.KafkaEventProducer;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.event.CommentEventRecord;
import faang.school.postservice.event.PostFollowersEventRecord;
import faang.school.postservice.event.PostViewEventRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventBuilder {
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