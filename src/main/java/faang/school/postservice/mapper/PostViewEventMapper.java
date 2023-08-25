package faang.school.postservice.mapper;

import faang.school.postservice.dto.redis.PostViewEventDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostViewEventMapper {

    @Mapping(target = "postId", source = "post.id")
    PostViewEventDto toDto(Post post);

    @Mapping(target = "id", source = "postId")
    Post toEntity(PostViewEventDto eventDto);
}
