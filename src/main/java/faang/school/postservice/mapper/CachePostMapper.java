package faang.school.postservice.mapper;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.events.CommentEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.RedisPost;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CachePostMapper {

    @Mapping(source = "comments", target = "commentEvents", qualifiedByName = "commentEvents")
    @Mapping(source = "likes", target = "likes", qualifiedByName = "countTotalLikes")
    RedisPost toCache(Post post);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "content", source = "content")
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "projectId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "likes", target = "likes", qualifiedByName = "countTotalLikes")
    @Mapping(source = "comments", target = "commentEvents", qualifiedByName = "commentEvents")
    void update(Post post, @MappingTarget RedisPost redisPost);

    @Named("commentEvents")
    default List<CommentEvent> getCommentDto(List<Comment> comments) {
        CommentMapperImpl commentMapper = new CommentMapperImpl();
        return comments.stream()
                .map(commentMapper::toEvent)
                .collect(Collectors.toList());
    }

    @Named("countTotalLikes")
    default long countTotalLikes(List<Like> likes){
        return likes == null ? 0 : likes.size();
    }
}
