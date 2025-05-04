package faang.school.postservice.mapper;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.PostViewEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostViewMapper {

    @Mapping(source = "id", target = "postId")
    PostViewEvent toEvent(Post post);
}
