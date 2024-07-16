package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "likes", target = "likesId", qualifiedByName = "listLikesToLong")
    CommentDto toDto(Comment comment);
    Comment toEntity(CommentDto commentDto);

    @Named("listLikesToLong")
    default List<Long> listLikesToLong(List<Like> likes){
        return likes.stream().map(Like::getId).toList();
    }
}
