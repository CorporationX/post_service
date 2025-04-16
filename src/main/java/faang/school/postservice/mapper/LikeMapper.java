package faang.school.postservice.mapper;

import faang.school.postservice.dto.CommentLikeDto;
import faang.school.postservice.dto.PostLikeDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LikeMapper {

    @Mapping(target = "post", ignore = true)
    @Mapping(target = "comment", ignore = true)
    Like fromPostLikeDtoToEntity(PostLikeDto dto);

    @Mapping(target = "post", ignore = true)
    @Mapping(target = "comment", ignore = true)
    Like fromCommentLikeDtoToEntity(CommentLikeDto dto);

    @Mapping(source = "post.id", target = "postId")
    PostLikeDto toPostLikeDto(Like like);

    @Mapping(source = "comment.id", target = "commentId")
    CommentLikeDto toCommentLikeDto(Like like);
}
