package faang.school.postservice.mapper;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostFeedDto;
import faang.school.postservice.events.PostViewEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostForCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class PostMapper {
    @Autowired
    protected UserContext userContext;

    @Value("${cache.last_comments_amount}")
    private long lastCommentsAmount;

    @Mapping(source = "likes", target = "likes", ignore = true)
    public abstract Post toEntity(PostDto dto);

    @Mapping(target = "likes", source = "likes", qualifiedByName = "sizeToLong")
    @Mapping(source = "comments", target = "commentIds", qualifiedByName = "mapCommentsToCommentsId")
    public abstract PostDto toDto(Post post);

    public abstract List<PostDto> toDtos(List<Post> posts);

    @Mapping(target = "userId", expression = "java(userContext.getUserId())")
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "authorId", source = "post.authorId")
    @Mapping(target = "viewedAt", expression = "java(java.time.LocalDateTime.now())")
    public abstract PostViewEvent toEvent(Post post);

    @Mapping(source = "comments", target = "lastCommentIds", qualifiedByName = "mapCommentsToLastCommentsId")
    @Mapping(source = "comments", target = "commentsAmount", qualifiedByName = "mapToAmount")
    @Mapping(source = "likes", target = "likesAmount", qualifiedByName = "sizeToLong")
    public abstract PostForCache toPostForCache(Post post);

    public abstract PostFeedDto toPostDtoForFeedFromPostForCache(PostForCache postForCache);

    @Mapping(source = "commentIds", target = "commentsAmount", qualifiedByName = "mapToAmount")
    @Mapping(source = "likes", target = "likesAmount")
    public abstract PostFeedDto toPostDtoForFeedFromPostDto(PostDto postDto);

    @Named("sizeToLong")
    Long sizeToLong(List<?> list) {
        if (list == null) {
            return 0L;
        }
        return (long) list.size();
    }

    @Named("mapToAmount")
    int mapToAmount(List<?> list) {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Named("mapCommentsToCommentsId")
    List<Long> mapCommentsToCommentsId(List<Comment> comments) {
        return comments.stream().map(Comment::getId).toList();
    }

    @Named("mapCommentsToLastCommentsId")
    List<Long> mapCommentsToLastCommentsId(List<Comment> comments) {
        return comments.stream().map(Comment::getId).limit(lastCommentsAmount).toList();
    }
}
