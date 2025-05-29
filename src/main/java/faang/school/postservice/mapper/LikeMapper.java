package faang.school.postservice.mapper;

import faang.school.postservice.dto.like.LikeCommentResponseDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikePostResponseDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LikeMapper {
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "commentId", source = "comment.id")
    LikeDto toDto(Like like);

    LikePostResponseDto toPostResponseDto(LikeDto like);

    LikeCommentResponseDto toCommentResponseDto(LikeDto like);
}
