package faang.school.postservice.redis.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.model.PostCache;
import faang.school.postservice.redis.repository.CommentCacheRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.redis.support.collections.RedisZSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Comparator.reverseOrder;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostCacheMapper {
    @Mapping(source = "likes", target = "likes", qualifiedByName = "countTotalLikes")
    @Mapping(source = "comments", target = "comments", qualifiedByName = "commentToThreeLastCommentId")
    PostCache toPostCache(PostDto postDto);

    PostDto toDto(PostCache postCache);

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