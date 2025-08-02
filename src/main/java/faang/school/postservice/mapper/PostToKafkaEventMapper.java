package faang.school.postservice.mapper;

import faang.school.postservice.dto.kafka.KafkaPostEventDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapping;

public interface PostToKafkaEventMapper {

    @Mapping(target = "likeCount", expression = "java(post.getLikes() != null ? post.getLikes().size() : 0L)")
    @Mapping(target = "commentCount", expression = "java(post.getComments() != null ? post.getComments().size() : 0L)")
    KafkaPostEventDto postToKafkaEvent(Post post);
}
