package faang.school.postservice.mapper;


import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

//    @Mapping(target = "likes", ignore = true)
    Comment toComment(CommentDto commentDto);

//    @Mapping(target = "likes", ignore = true)
    CommentDto toCommentDto(Comment comment);

//    @AfterMapping
//    default void mapLikes(Comment comment, @MappingTarget CommentDto commentDto) {
//        List<Long> likes = comment.getLikes().stream()
//                .map(Like::getId)
//                .toList();
//
//        commentDto.setLikes(likes);
//    }
}
