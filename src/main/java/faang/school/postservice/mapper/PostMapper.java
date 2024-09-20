package faang.school.postservice.mapper;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.events.PostViewEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostForCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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

    public abstract PostForCache toPostForCache(Post post);

    @Named("sizeToLong")
    Long sizeToLong(List<?> list) {
        if (list == null) {
            return 0L;
        }
        return (long) list.size();
    }
}
