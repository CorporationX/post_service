package faang.school.postservice.mapper;

import faang.school.postservice.dto.like.LikeResponseDto;
import faang.school.postservice.events.LikeEvent;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "comment.id", target = "commentId")
    LikeResponseDto toResponseDto(Like like);

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "post.authorId", target = "authorId")
    LikeEvent toEvent(Like like);
}
