package faang.school.postservice.mapper;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.event.LikeEvent;
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

    @Mapping(source = "commentId", target = "comment.id")
    @Mapping(source = "postId", target = "post.id")
    Like toEntity(LikeDto like);

    LikeEvent toEvent(LikeDto likeDto);

    List<LikeDto> toLikesDto(List<Like> likes);
}