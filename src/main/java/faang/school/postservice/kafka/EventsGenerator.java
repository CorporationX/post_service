package faang.school.postservice.kafka;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.kafka.events.CommentEvent;
import faang.school.postservice.kafka.events.PostFollowersEvent;
import faang.school.postservice.kafka.events.PostLikeEvent;
import faang.school.postservice.kafka.events.PostViewEvent;
import faang.school.postservice.kafka.producer.KafkaEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventsGenerator {
    private final KafkaEventProducer kafkaEventProducer;
    private final UserServiceClient userServiceClient;

    public void generateAndSendPostFollowersEvent(PostDto postDto){
        var author = userServiceClient.getUser(postDto.getAuthorId());
        var event = PostFollowersEvent.builder()
                .authorId(postDto.getAuthorId())
                .followersIds(author.getFollowers())
                .publishedAt(postDto.getPublishedAt())
                .build();

        kafkaEventProducer.sendPostFollowersEvent(event);
    }

    public void generateAndSendPostViewEvent(PostDto postDto){
        var event = PostViewEvent.builder()
                .postId(postDto.getId())
                .build();
        kafkaEventProducer.sendPostViewEvent(event);
    }

    public void generateAndSendLikeEvent(PostDto postDto){
        var event = PostLikeEvent.builder()
                .id(postDto.getId())
                .authorId(postDto.getAuthorId())
                .build();
        kafkaEventProducer.sendLikeEvent(event);
    }

    public void generateAndSendCommentEventToKafka(CommentDto commentDto){
        var event = CommentEvent.builder()
                .commentDto(commentDto)
                .content(commentDto.getContent())
                .postId(commentDto.getPostId())
                .authorId(commentDto.getAuthorId())
                .build();
        kafkaEventProducer.sendCommentEvent(event);
    }
}