package faang.school.postservice.mapper;

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
    Like toDto(LikeDto likeDto);

    @Mapping(target = "commentId", source = ".")
    LikeDto toEntity(Like like);

    default Long toCommentId(Like like) {
        return like.getComment().getId();
    }
}
