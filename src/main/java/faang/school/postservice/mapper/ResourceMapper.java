package faang.school.postservice.mapper;

import faang.school.postservice.dto.resource.ResourceRequest;
import faang.school.postservice.dto.resource.ResourceResponse;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.Post;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface ResourceMapper {

    @Mapping(source = "postId", target = "post")
    Resource toEntity(ResourceRequest request);

    @Mapping(source = "post.id", target = "postId")
    ResourceResponse toResponse(Resource resource);

    default Post map(Long postId) {
        if (postId == null) {
            return null;
        }
        Post post = new Post();
        post.setId(postId);
        return post;
    }
}