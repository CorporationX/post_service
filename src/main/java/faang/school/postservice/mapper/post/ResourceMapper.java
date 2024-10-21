package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.resource.PostResourceDto;
import faang.school.postservice.dto.resource.PreviewPostResourceDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.mapstruct.*;

import java.io.InputStream;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ResourceMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "key", target = "key")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "size", target = "size")
    @Mapping(source = "type", target = "type")
    ResourceDto toDto(Resource resource);

    @Mapping(source = "resourceDto.id", target = "id")
    @Mapping(source = "resourceDto.key", target = "key")
    @Mapping(source = "resourceDto.name", target = "name")
    @Mapping(source = "resourceDto.size", target = "size")
    @Mapping(source = "resourceDto.type", target = "type")
    @Mapping(source = "postId", target = "post", qualifiedByName = "mapPostId")
    Resource toEntity(long postId, ResourceDto resourceDto);

    @Named("mapPostId")
    default Post mapPostId(long postId) {
        return Post.builder().id(postId).build();
    }

    @Mapping(target = "id", source = "resource.id")
    @Mapping(target = "name", source = "resource.name")
    PreviewPostResourceDto toPreviewPostResourceDto(ResourceDto resource);

    @Mapping(target = "resource", source = "data")
    PostResourceDto toPostResourceDto(Resource resource, InputStream data);
}
