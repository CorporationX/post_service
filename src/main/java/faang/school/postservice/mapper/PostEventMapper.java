package faang.school.postservice.mapper;

import faang.school.postservice.dto.hash.PostHash;
import faang.school.postservice.dto.event_broker.PostEvent;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostEventMapper {
    @Mapping(target = "postId", source = "id")
    @Mapping(target = "userAuthorId", source = "authorId")
    @Mapping(target = "projectAuthorId", source = "projectId")
    PostEvent toPostEvent(Post post);

    @Mapping(target = "postId", source = "id")
    @Mapping(target = "userAuthorId", source = "authorId")
    @Mapping(target = "projectAuthorId", source = "projectId")
    PostEvent toPostEvent(PostDto postDto);

    PostHash toPostHash(PostEvent post);
}