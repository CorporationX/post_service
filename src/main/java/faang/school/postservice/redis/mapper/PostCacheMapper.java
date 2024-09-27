package faang.school.postservice.redis.mapper;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.model.PostCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostCacheMapper {
    @Mapping(source = "likes", target = "likes", qualifiedByName = "countTotalLikes")
    @Mapping(source = "comments", target = "comments", qualifiedByName = "commentToThreeLastCommentId")
    PostCache toPostCache(Post post);

    @Named("countTotalLikes")
    default Integer countTotalLikes(List<Like> likes){
        return likes == null ? 0 : likes.size();
    }

    @Named("commentToThreeLastCommentId")
    default List<Long> commentToThreeLastCommentId(List<Comment> comments) {
        return comments == null ? List.of() :
                comments.stream()
                        .map(Comment::getId)
                        .sorted(reverseOrder())
                        .limit(3)
                        .toList();
    }
}