package faang.school.postservice.mapper;


import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.model.resource.Resource;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    ResourceDto toDto(Resource resource);

    Resource toEntity(ResourceDto resourceDto);
}
