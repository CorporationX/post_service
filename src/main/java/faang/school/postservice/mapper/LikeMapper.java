package faang.school.postservice.mapper;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.event.LikeEvent;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {
    @Mapping(source = "postId", target = "post.id")
    @Mapping(source = "commentId", target = "comment.id")
    Like toEntity(LikeDto dto);

    @Mapping(target = "postId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    LikeDto toDto(Like like);

    @Mapping(target = "authorId", source = "post.authorId")
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "timeStamp", source = "createdAt")
    @Mapping(target = "userId", source = "userId")
    LikeEvent toEvent(Like like);
}
