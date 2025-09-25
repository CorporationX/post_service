package faang.school.postservice.mapper;

import faang.school.postservice.dto.avro.PostPublishedEventAvro;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.time.ZoneOffset;

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

    default PostPublishedEventAvro toAvro(Post post) {
        return new PostPublishedEventAvro(
                String.valueOf(post.getId()),
                String.valueOf(post.getAuthorId()),
                String.valueOf(post.getProjectId()),
                post.getPublishedAt().atZone(ZoneOffset.UTC).toInstant()
        );
    }

    void update(@MappingTarget Post post, PostUpdateDto updateDto);
}