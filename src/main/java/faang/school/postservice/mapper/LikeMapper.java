package faang.school.postservice.mapper;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * @author Alexander Bulgakov
 */

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {

    @Mapping(source = "comment.id", target = "commentId")
    @Mapping(source = "post.id", target = "postId")
    LikeDto toDto(Like entity);

    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "post", ignore = true)
    Like toEntity(LikeDto dto);
}
