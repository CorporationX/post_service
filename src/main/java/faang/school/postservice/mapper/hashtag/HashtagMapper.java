package faang.school.postservice.mapper.hashtag;

import faang.school.postservice.dto.hashtag.HashtagViewDto;
import faang.school.postservice.model.Hashtag;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * HashtagMapper — описание интерфейса.
 * <p>
 * TODO: описать, какие обязанности реализует интерфейс.
 * </p>
 *
 * @author Myrza
 * @since 08.08.2025
 */

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HashtagMapper {
    HashtagViewDto toViewDto(Hashtag entities);
    List<HashtagViewDto> toViewDtoList (List<Hashtag> entities);
}
