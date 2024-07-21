package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.event.LikeEventDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.model.Like;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {
    @Mapping(source = "comment.id",target = "commentId")
    @Mapping(source = "post.id",target = "postId")
    LikeDto toDto(Like like);

    @Mapping(source = "post.id",target = "postId")
    LikeEventDto toEventDto(Like like);
    
    Like toModel(LikeDto dto);
}
