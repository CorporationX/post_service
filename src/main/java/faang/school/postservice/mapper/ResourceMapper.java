package faang.school.postservice.mapper;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    ResourceDto toDto (Resource resource);

    List<ResourceDto> toDtos (List<Resource>  resources);
}
