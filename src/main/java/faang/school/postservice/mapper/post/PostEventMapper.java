package faang.school.postservice.mapper.post;

import faang.school.postservice.mapper.resource.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.PostEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = ResourceMapper.class)
public interface PostEventMapper {
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "followersId", ignore = true)
    @Mapping(target = "likesCount", ignore = true)
    @Mapping(target = "viewsCount", ignore = true)
    @Mapping(target = "ttl", ignore = true)
    PostEvent toEvent(Post post);

    Post toEntity(PostEvent event);
}
