package faang.school.postservice.mapper;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.event.LikeEventDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {
    Like toEntity(LikeDto likeDto);

    @Mapping(target = "commentId", source = "comment.id")
    @Mapping(target = "postId", source = "post.id")
    LikeDto toDto(Like like);

    @Mapping(target = "commentId", source = "comment.id")
    @Mapping(target = "postId", source = "post.id")
    List<LikeDto> toDtos(List<Like> likes);

    @Mapping(target = "postAuthorId", source = "post.authorId")
    @Mapping(target = "likerId", source = "userId")
    @Mapping(target = "postId", source = "post.id")
    LikeEventDto toEventDto(Like like);
}
