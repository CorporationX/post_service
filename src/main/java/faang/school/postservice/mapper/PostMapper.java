package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "resourceKeys", expression = "java(mapKeys(post))")
    PostDto toDto(Post post);

    @Mapping(target = "resources", ignore = true)
    Post toEntity(PostDto dto);

    default List<String> mapKeys(Post post) {
        if (post.getResources() == null) {
            return List.of();
        }
        return post.getResources().stream()
                .map(Resource::getKey)
                .toList();
    }
}
