package faang.school.postservice.mapper;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface LikeMapper {

    @Mapping(target = "postId", expression = "java(like.getPost() != null ? like.getPost().getId() : null)")
    @Mapping(target = "commentId", expression = "java(like.getComment() != null ? like.getComment().getId() : null)")
    LikeDto toDto(Like like);
}
