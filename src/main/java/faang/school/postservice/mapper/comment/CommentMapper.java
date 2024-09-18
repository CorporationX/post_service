package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "likesIds", source = "likes", qualifiedByName = "likesToLikesIds")
    CommentResponseDto toResponseDto(Comment comment);

    @Mapping(target = "post.id", source = "postId")
    Comment toEntity(CommentRequestDto dto);

    List<CommentResponseDto> toResponseDto(List<Comment> comments);

    @Named("likesToLikesIds")
    default List<Long> likesToLikesIds(List<Like> likes) {
        if (likes == null) {
            return new ArrayList<>();
        }
        return likes.stream()
                .map(Like::getId)
                .toList();
    }
}