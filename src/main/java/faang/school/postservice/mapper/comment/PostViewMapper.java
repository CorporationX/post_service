package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.event.post.PostEvent;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostViewMapper {
    @Mapping(source = "id", target = "postId")
    PostViewDto toDto(Post post);

    PostEvent toPostEvent(Post post);
}