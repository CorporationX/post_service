package faang.school.postservice.mapper;

import faang.school.postservice.dto.like.LikeCommentResponseDto;
import faang.school.postservice.dto.like.LikePostResponseDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LikeMapper {
    @Mapping(target = "postId", source = "post.id")
    LikePostResponseDto toPostResponseDto(Like like);

    @Mapping(target = "commentId", source = "comment.id")
    LikeCommentResponseDto toCommentResponseDto(Like like);
}
