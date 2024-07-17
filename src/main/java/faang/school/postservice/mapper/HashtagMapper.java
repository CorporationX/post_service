package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.HashtagDto;
import faang.school.postservice.model.Hashtag;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HashtagMapper {
    HashtagDto toDto(Hashtag hashtag);

    Hashtag toEntity(HashtagDto hashtagDto);

    List<HashtagDto> toDto(List<Hashtag> hashtag);
}
