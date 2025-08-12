package faang.school.postservice.mapper.hashtag;

import faang.school.postservice.dto.hashtag.HashtagViewDto;
import faang.school.postservice.model.Hashtag;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct-маппер для преобразования сущностей {@link Hashtag} в DTO {@link HashtagViewDto}.
 * <p>
 * Используется для изоляции слоя представления от слоя данных
 * и автоматической генерации кода маппинга.
 * </p>
 *
 * <p>Конфигурация:</p>
 * <ul>
 *   <li>{@code componentModel = SPRING} — маппер будет Spring-бином и доступен для автосвязывания.</li>
 *   <li>{@code nullValuePropertyMappingStrategy = IGNORE} — null-значения в источнике не будут перезаписывать поля в целевом объекте.</li>
 *   <li>{@code unmappedTargetPolicy = IGNORE} — игнорировать поля целевого объекта, для которых нет соответствия в источнике.</li>
 * </ul>
 *
 * @author Myrza
 * @since 08.08.2025
 */

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HashtagMapper {
    HashtagViewDto toViewDto(Hashtag entities);

    List<HashtagViewDto> toViewDtoList(List<Hashtag> entities);
}
