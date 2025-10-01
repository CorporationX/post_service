package faang.school.postservice.mapper;

import faang.school.postservice.dto.avro.PostPublishedEventAvro;
import faang.school.postservice.dto.feed.PostFeedDto;
import faang.school.postservice.dto.post.PostCountsProjection;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.time.ZoneOffset;
import java.util.List;

/**
 * Маппер для преобразования сущности в DTO и наоборот, для обновления сущности и преобразования
 * из сущности в ивент {@link PostPublishedEventAvro}
 *
 * @author Linempy
 * @since 26.07.2025
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    Post toEntity(PostCreateDto createDto);

    PostViewDto toViewDto(Post post);

    void update(@MappingTarget Post post, PostUpdateDto updateDto);

    default PostPublishedEventAvro toAvro(Post post) {
        return new PostPublishedEventAvro(
                String.valueOf(post.getId()),
                String.valueOf(post.getAuthorId()),
                String.valueOf(post.getProjectId()),
                post.getPublishedAt().atZone(ZoneOffset.UTC).toInstant()
        );
    }

    default PostRedisDto toRedisDto(Post post, PostCountsProjection countsProjection) {
        return new PostRedisDto(
                post.getId(),
                post.getContent(),
                post.getAuthorId(),
                post.getProjectId(),
                countsProjection.getLikeCount(),
                countsProjection.getCommentCount(),
                post.getPublishedAt()
        );
    }

    @Mapping(target = "authorUser", ignore = true)
    @Mapping(target = "comments", ignore = true)
    PostFeedDto toFeedDto(PostRedisDto post);

    default List<PostFeedDto> toFeedDtos(List<PostRedisDto> posts) {
        return posts.stream()
                .map(this::toFeedDto)
                .toList();
    }

}