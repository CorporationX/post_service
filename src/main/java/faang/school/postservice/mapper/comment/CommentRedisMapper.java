package faang.school.postservice.mapper.comment;


import faang.school.postservice.dto.comment.CommentRedisDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentRedisMapper {
    CommentRedisDto toCommentRedisDto(Comment comment);
}
