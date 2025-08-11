package faang.school.postservice.mapper.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.model.Resource;

public class ResourceMapper {

    public static ResourceDto resourceToResourceDto (Resource resource){
        return ResourceDto.builder()
                .name(resource.getName())
                .type(resource.getType())
                .size(resource.getSize())
                .build();
    }

    public static Resource resourceDtoToResource(ResourceDto dto) {
        return Resource.builder()
                .name(dto.getName())
                .type(dto.getType())
                .size(dto.getSize())
                .build();
    }
}
