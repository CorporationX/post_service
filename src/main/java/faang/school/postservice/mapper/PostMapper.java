package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(source = "resources", target = "resourceIds", qualifiedByName = "getResourceIds")
    PostDto toDto(Post post);

    Post toEntity(PostDto postDto);

    List<PostDto> toDtoList(List<Post> postList);

    List<Post> toEntityList(List<PostDto> postDtoList);

    @Named("getResourceIds")
    default List<Long> getResourceIds(List<Resource> resources) {
        if (resources == null) {
            return null;
        }
        return resources.stream().map(Resource::getId).toList();
    }

}
