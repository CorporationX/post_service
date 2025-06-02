package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentCacheDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentCacheMapper {

    @Mapping(source = "likes", target = "likeCount", qualifiedByName = "countLikes")
    @Mapping(source = "post.id", target = "postId")
    CommentCacheDto commentToCacheDto(Comment comment);

    @Named("countLikes")
    default int countLikes(List<Like> likes) {
        return likes != null ? likes.size() : 0;
    }
}