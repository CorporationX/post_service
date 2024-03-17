package faang.school.postservice.mapper;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "likeIds", source = "likes", qualifiedByName = "likesToLikeIds")
    CommentDto toDto(Comment comment);

    Comment toEntity(CommentDto commentDto);

    List<CommentDto> toDto(List<Comment> comments);

    @Named("likesToLikeIds")
    default List<Long> likesToLikeIds(List<Like> likes) {
        return likes.stream()
                .map(Like::getId)
                .collect(Collectors.toList());
    }
}
