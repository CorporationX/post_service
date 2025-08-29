package faang.school.postservice.mapper;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface LikeMapper {

    Like toPostLike(long userId, Post post);

    Like toCommentLike(long userId, Comment comment);
}
