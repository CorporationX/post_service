package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.request.PostCreationRequest;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    Post toPostFromCreationRequest(PostCreationRequest postCreationRequest);

    @Mapping(source = "likes", target = "likes", qualifiedByName = "getLikesCount")
    @Mapping(source = "resources", target = "resources", qualifiedByName = "getResourcesKey")
    PostDto toPostDto(Post post);

    List<PostDto> toPostDtoList(List<Post> posts);

    @Named("getLikesCount")
    default int getLikesCount(List<Like> likes) {
        return likes.size();
    }

    @Named("getResourcesKey")
    default List<String> getResourcesKey(List<Resource> resources) {
        if (resources == null) {
            return new ArrayList<>();
        }
        return resources.stream()
                .map(Resource::getKey)
                .toList();
    }
}