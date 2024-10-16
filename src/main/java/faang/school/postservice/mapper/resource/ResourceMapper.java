package faang.school.postservice.mapper.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.model.resource.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    @Mapping(target = "size", expression = "java(getSize(resource.getSize()))")
    ResourceDto toResourceDto(Resource resource);

    List<ResourceDto> toResourceDtoList(List<Resource> resourceList);

    default String getSize(long size) {
        if (size < 1024) {
            return size + " bytes";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f kilobytes", size / 1024.0);
        } else {
            return String.format("%.2f megabytes", size / (1024.0 * 1024));
        }
    }
}
