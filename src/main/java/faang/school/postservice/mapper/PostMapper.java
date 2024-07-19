package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

//TODO    @Mapping(source = "resources", target = "resourceIds", qualifiedByName = "toResourceIds")
    PostDto toDto(Post post);

    @Mapping(target = "resources", ignore = true)
    Post toEntity(PostDto postDto);

    @Named("toResourceIds")
    default List<Long> toResourceIds(List<Resource> resources) {
        if (Objects.nonNull(resources)) {
            return resources.stream()
                    .map(Resource::getId)
                    .toList();
        }
        return new ArrayList<>();
    }
}
