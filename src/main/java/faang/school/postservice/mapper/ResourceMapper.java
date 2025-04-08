package faang.school.postservice.mapper;

import faang.school.postservice.dto.resource.ResourceResponseDto;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResourceMapper {
    ResourceResponseDto toResourceDto(Resource resource);
}
