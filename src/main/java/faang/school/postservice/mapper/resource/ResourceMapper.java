package faang.school.postservice.mapper.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    @Mapping(source = "post.id", target = "postId")
    ResourceDto toDto(Resource resource);

    List<ResourceDto> toDtoList(List<Resource> resources);
}