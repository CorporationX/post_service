package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.DraftPostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.resource.PreviewPostResourceDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.mapstruct.*;

import java.util.Collections;
import java.util.List;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface PostMapper {

    PostDto fromDraftPostDto(DraftPostDto draftPostDto);

    @Mapping(source = "resources", target = "resources", qualifiedByName = "mapResources")
    PostDto toDto(Post post);

    @Named("mapResources")
    default List<PreviewPostResourceDto> mapResources(List<Resource> resources) {

        if (resources == null) {
            return Collections.emptyList();
        }

        return resources.stream()
                .map(
                        r -> new PreviewPostResourceDto(
                                r.getId(),
                                r.getName()
                        )
                )
                .toList();
    }

    @Mapping(source = "postDto.resources", target = "resources", qualifiedByName = "mapPreviewPostResourceDto")
    Post toEntity(PostDto postDto);

    @Named("mapPreviewPostResourceDto")
    default List<Resource> mapPreviewPostResourceDto(List<PreviewPostResourceDto> resourceDtos) {
        if (resourceDtos == null) {
            return null;
        }
        return resourceDtos.stream()
                .map(resDto -> Resource.builder().id(resDto.getId()).build())
                .toList();
    }
}
