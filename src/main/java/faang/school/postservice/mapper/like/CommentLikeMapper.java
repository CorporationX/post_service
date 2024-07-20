package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.CommentLikeDto;
import faang.school.postservice.kafka.event.like.CommentLikeKafkaEvent;
import faang.school.postservice.kafka.event.State;
import faang.school.postservice.model.CommentLike;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentLikeMapper {

    @Mapping(source = "commentId", target = "comment.id")
    CommentLike toEntity(CommentLikeDto likeDto);

    @Mapping(source = "comment.id", target = "commentId")
    CommentLikeDto toDto(CommentLike like);

    @Mapping(source = "like.comment.id", target = "commentId")
    CommentLikeKafkaEvent toKafkaEvent(CommentLike like, State state);
}
