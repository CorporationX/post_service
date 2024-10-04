package faang.school.postservice.mapper.post;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.Post.PostDto;
import faang.school.postservice.dto.comment.LastCommentDto;
import faang.school.postservice.events.PostEvent;
import faang.school.postservice.events.PostViewEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisPost;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class PostMapper {
    @Autowired
    protected UserContext userContext;

    @Mapping(source = "likes", target = "likes", ignore = true)
    public abstract Post toEntity(PostDto dto);

    @Mapping(target = "likes", source = "likes", qualifiedByName = "sizeToLong")
    public abstract PostDto toDto(Post post);

    @Mapping(target = "userId", expression = "java(userContext.getUserId())")
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "authorId", source = "post.authorId")
    @Mapping(target = "viewedAt", expression = "java(java.time.LocalDateTime.now())")
    public abstract PostViewEvent toEvent(Post post);

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "authorId", source = "post.authorId")
    public abstract PostEvent toPostEvent(Post post);

    @Named("sizeToLong")
    Long sizeToLong(List<?> list) {
        if (list == null) {
            return 0L;
        }
        return (long) list.size();
    }

    @Mapping(source = "content", target = "postInfoDto.postContent")
    @Mapping(source = "likes", target = "postInfoDto.likes", qualifiedByName = "mapLikes")
    @Mapping(source = "updatedAt", target = "postInfoDto.updatedAt")
    @Mapping(source = "authorId", target = "postInfoDto.dto.id")
    @Mapping(source = "comments", target = "postInfoDto.comments", qualifiedByName = "mapComments")
    public abstract RedisPost toRedisEntity(Post post);

    @Named("mapComments")
    LinkedHashSet<LastCommentDto> mapComments(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return new LinkedHashSet<>();
        }
        return comments.stream().map(this::mapComment)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Mapping(source = "content", target = "comment")
    @Mapping(source = "updatedAt", target = "createdAt")
    public abstract LastCommentDto mapComment(Comment comment);

    @Named("mapLikes")
    long mapLikes(List<Like> likes) {
        return likes != null ? likes.size() : 0L;
    }
}
