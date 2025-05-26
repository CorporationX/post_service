package faang.school.postservice.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.model.Like;

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
    List<LikeDto> toDtois(List<Like> likes);
}
