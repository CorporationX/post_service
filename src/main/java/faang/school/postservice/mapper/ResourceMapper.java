package faang.school.postservice.mapper;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.model.Resource;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    ResourceDto toDto(Resource resource);

    Resource toEntity(ResourceDto resourceDto);

    List<ResourceDto> toListDto(List<Resource> resources);
}
