package faang.school.postservice.mapper;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.model.ResourceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "resourceId", source = "id")
    ResourceDto toResourceDto(ResourceEntity resourceEntity);

    List<ResourceDto> toResourceDtoList(List<ResourceEntity> resourceEntities);
}
