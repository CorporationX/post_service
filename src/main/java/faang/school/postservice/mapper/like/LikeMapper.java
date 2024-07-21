package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.event.LikeEventDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {
    @Mapping(source = "comment.id", target = "commentId")
    @Mapping(source = "post.id", target = "postId")
    LikeDto toDto(Like like);

    @Mapping(source = "userId", target = "authorId")
    @Mapping(source = "id", target = "likeId")
    @Mapping(source = "post.id", target = "postId")
    LikeEventDto toEventDto(Like like);

    Like toModel(LikeDto dto);

    List<LikeDto> toDto(List<Like> likes);
}
