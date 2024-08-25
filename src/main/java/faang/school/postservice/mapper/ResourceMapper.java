package faang.school.postservice.mapper;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {
    @Mapping(source = "post.id", target = "postId")
    ResourceDto toDto(Resource resource);
    @Mapping(source = "postId", target = "post.id")
    Resource toEntity(ResourceDto resourceDto);
}
