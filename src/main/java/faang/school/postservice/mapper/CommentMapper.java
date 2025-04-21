package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {LikeMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    @Mapping(target = "likes", source = "likes")
    @Mapping(target = "post.id", source = "postId")
    Comment toEntity(CommentDto commentDto);

    @Mapping(target = "likes", source = "likes")
    @Mapping(target = "postId", source = "post.id")
    CommentDto toDto(Comment comment);

    List<Comment> toEntity(List<CommentDto> dtos);

    List<CommentDto> toDto(List<Comment> entities);
}
