package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "likes", source = "comment", qualifiedByName = "countLikes")
    CommentDto toDto(Comment comment);

    @Mapping(target = "likes", ignore = true)
    Comment toEntity(CommentDto commentDto);

    @Named("countLikes")
    static long countLikes(Comment comment) {
        return comment.getLikes() != null ? comment.getLikes().size() : 0;
    }
}
