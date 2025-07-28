package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Маппер для преобразования сущности в DTO и наоборот, а также обновления сущности
 *
 * @author Linempy
 * @since 26.07.2025
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    Post toEntity(PostCreateDto createDto);

    PostViewDto toViewDto(Post post);

    void update(@MappingTarget Post post, PostUpdateDto updateDto);

}