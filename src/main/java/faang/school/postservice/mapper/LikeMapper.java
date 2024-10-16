package faang.school.postservice.mapper;

import faang.school.postservice.model.dto.LikeDto;
import faang.school.postservice.model.entity.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {

    @Mapping(source = "postId", target = "post.id")
    @Mapping(source = "commentId", target = "comment.id")
    Like toEntity(LikeDto dto);

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "comment.id", target = "commentId")
    LikeDto toDto(Like entity);
}