package faang.school.postservice.mapper;

import faang.school.postservice.dto.file.FileReadDto;
import faang.school.postservice.model.File;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileMapper {

    FileReadDto toDto(File file);
}
