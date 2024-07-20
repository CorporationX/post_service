package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.PostLikeDto;
import faang.school.postservice.kafka.event.like.PostLikeKafkaEvent;
import faang.school.postservice.kafka.event.State;
import faang.school.postservice.model.PostLike;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostLikeMapper {

    @Mapping(source = "postId", target = "post.id")
    PostLike toEntity(PostLikeDto likeDto);

    @Mapping(source = "post.id", target = "postId")
    PostLikeDto toDto(PostLike like);

    @Mapping(source = "like.post.id", target = "postId")
    PostLikeKafkaEvent toKafkaEvent(PostLike like, State state);
}
