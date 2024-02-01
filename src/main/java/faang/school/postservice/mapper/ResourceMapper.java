package faang.school.postservice.mapper;

import faang.school.postservice.dto.s3.ResourceDto;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {
    ResourceDto toResourceDto(Resource resource);

     Resource toResourceEntity(ResourceDto resourceDto);
}
