package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Маппер для преобразования объектов сущности {@link Post} и DTO.
 * <p>
 * Использует MapStruct для автоматической генерации кода преобразований.
 * Конфигурация:
 * <ul>
 *   <li>{@code componentModel = "spring"} – маппер регистрируется как Spring-бин</li>
 *   <li>{@code nullValuePropertyMappingStrategy = IGNORE} – при обновлении игнорируются поля с {@code null}</li>
 *   <li>{@code unmappedTargetPolicy = IGNORE} – игнорируются поля, для которых нет маппинга</li>
 * </ul>
 *
 * @author Myrza
 * @since 25.07.2025
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    Post toEntity(PostViewDto dto);

    Post toEntity(PostCreateDto createDto);

    void update(PostUpdateDto updateDto, @MappingTarget Post entity);

    @Mapping(target = "likeCount", source = "likeCount")
    PostViewDto toViewDto(Post entity);

    List<PostViewDto> toViewDtoList(List<Post> entities);
}
