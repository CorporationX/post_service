package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(source = "post.id", target = "postId")
    CommentDto toCommentDto(Comment comment);

    @Mapping(source = "likes", target = "likeIds", qualifiedByName = "mapIds")
    @Mapping(source = "post.id", target = "postId")
    ResponseCommentDto toResponseDto(Comment comment);

    Comment toEntity(CommentDto commentDto);

    @Named("mapIds")
    default List<Long> getLikeIds(List<Like> likes) {
        if (likes == null || likes.isEmpty()) {
            return Collections.emptyList();
        }
        return likes.stream()
                .map(Like::getId)
                .toList();
    }
}