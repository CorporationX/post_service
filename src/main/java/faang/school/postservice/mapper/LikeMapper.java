package faang.school.postservice.mapper;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LikeMapper {
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "commentId", source = "comment.id")
    LikeDto toDto(Like like);
}
