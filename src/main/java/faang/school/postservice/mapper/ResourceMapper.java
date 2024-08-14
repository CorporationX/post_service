package faang.school.postservice.mapper;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {
    Resource resourceDtoToResource(ResourceDto resourceDto);

    ResourceDto resourceToResourceDto(Resource resource);

    List<ResourceDto> resourceListToResourceDtoList(List<Resource> resourceList);
}
