package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper {
    Comment toComment(CommentDto createCommentDto);

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "likes", target = "likeCount", qualifiedByName = "countLikes")
    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "content", source = "newContent")
    void update(String newContent, @MappingTarget Comment comment);

    @Named("countLikes")
    default long countLikes(List<Like> likes) {
        if (likes != null) {
            return likes.size();
        }
        return 0;
    }
}
