package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.Collection;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CommentMapper {
    Comment toEntity(CommentRequestDto commentRequestDto);

    Collection<CommentResponseDto> toDtos(Collection<Comment> comments);

    @Mapping(target = "likes", source = "likes", qualifiedByName = "listOfLikesToIds")
    @Mapping(target = "postId", source = "post.id")
    CommentResponseDto toDto(Comment comment);

    @Named("listOfLikesToIds")
    @BeanMapping
    default Collection<Long> listOfLikesToIds(Collection<Like> likes) {
        if (likes != null) {
            return likes.stream().map(Like::getId).toList();
        }
        return new ArrayList<>();
    }
}
