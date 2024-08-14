package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostViewMapper {
    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "likes", target = "likesId", qualifiedByName = "listLikesToLong")
    CommentDto toDto(Comment comment);
}
