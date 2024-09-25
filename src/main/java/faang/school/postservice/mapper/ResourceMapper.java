package faang.school.postservice.mapper;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    ResourceDto toResourceDto(Resource resource);

    Resource toResourceEntity(ResourceDto resourceDto);

    List<ResourceDto> toResourceDtoList(List<Resource> resources);

    List<Resource> toResourceEntityList(List<ResourceDto> resourceDtos);

}
