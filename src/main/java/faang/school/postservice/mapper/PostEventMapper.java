package faang.school.postservice.mapper;

import faang.school.postservice.dto.redis.PostEventDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostEventMapper {

    @Mapping(target = "postId", source = "post.id")
    PostEventDto toDto(Post post);

    @Mapping(target = "id", source = "postId")
    Post toEntity(PostEventDto eventDto);
}
