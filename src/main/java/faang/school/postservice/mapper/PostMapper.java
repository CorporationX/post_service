package faang.school.postservice.mapper;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.messaging.events.PostPublishedEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(source = "resources", target = "resourceIds", qualifiedByName = "toResourceId")
    PostDto toDto(Post post);

    @Mapping(target = "resources", ignore = true)
    Post toEntity(PostDto postDto);

    List<PostDto> toDtoList(List<Post> posts);

    @Mapping(target = "followersIds", ignore = true)
    PostPublishedEvent toPostPublishedEvent(Post post);

    @Named("toResourceId")
    default List<Long> toResourceId(List<Resource> resources) {
        return resources != null ? resources.stream().map(Resource::getId).toList() : Collections.emptyList();
    }
}
