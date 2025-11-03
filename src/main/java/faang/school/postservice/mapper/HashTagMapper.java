package faang.school.postservice.mapper;

import faang.school.postservice.dto.HashTag.HashTagDto;
import faang.school.postservice.model.HashTag;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HashTagMapper {

    HashTag toHashTag(HashTagDto hashTagDto);

    HashTagDto toHashTagDto(HashTag hashTag);
}
