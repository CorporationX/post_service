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

        @Mapping(target = "postId", source = "post.id")
        @Mapping(target = "likeIds", source = "likes", qualifiedByName = "getLikeIds")
        CommentDto toDTO(Comment comment);

        @Mapping(target = "post", ignore = true)
        @Mapping(target = "likes", ignore = true)
        Comment toEntity(CommentDto commentDto);

    @Named("getLikeIds")
    default List<Long> getLikeIds(List<Like> likes) {
        if (likes == null) {
            return null;
        }
        return likes.stream().map(Like::getId).toList();
    }
    }

