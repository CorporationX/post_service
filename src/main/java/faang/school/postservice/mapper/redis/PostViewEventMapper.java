package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.redis.PostViewEventDto;
import faang.school.postservice.model.Post;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PostViewEventMapper {
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "authorId", source = "post.authorId")
    PostViewEventDto toDto(Post post);
}
