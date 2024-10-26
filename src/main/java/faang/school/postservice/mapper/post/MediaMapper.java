package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.media.MediaDto;
import faang.school.postservice.dto.resource.ResourceDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MediaMapper {
    @Mapping(target = "key", source = "key")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "size",source = "size")
    @Mapping(target = "type", source = "type")
    ResourceDto toResourceDto(MediaDto mediaDto);
}
