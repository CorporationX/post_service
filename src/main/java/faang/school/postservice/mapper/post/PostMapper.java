package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostCache;
import faang.school.postservice.dto.post.CreatePostRequestDto;
import faang.school.postservice.dto.post.PostFeedDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.UpdatePostRequestDto;
import faang.school.postservice.model.Post;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct-маппер между Post и DTO.
 * Игнорирует служебные поля при создании/обновлении.
 */
@Mapper(componentModel = "spring")
public interface PostMapper {

    PostResponseDto toDto(Post post);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "published", constant = "false"),
            @Mapping(target = "deleted", constant = "false"),
            @Mapping(target = "publishedAt", ignore = true),
            @Mapping(target = "likes", ignore = true),
            @Mapping(target = "comments", ignore = true),
            @Mapping(target = "albums", ignore = true),
            @Mapping(target = "ad", ignore = true),
            @Mapping(target = "resources", ignore = true),
            @Mapping(target = "scheduledAt", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true)
    })
    Post toEntity(CreatePostRequestDto dto);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "authorId", ignore = true),
            @Mapping(target = "projectId", ignore = true),
            @Mapping(target = "published", ignore = true),
            @Mapping(target = "publishedAt", ignore = true),
            @Mapping(target = "deleted", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "likes", ignore = true),
            @Mapping(target = "comments", ignore = true),
            @Mapping(target = "albums", ignore = true),
            @Mapping(target = "ad", ignore = true),
            @Mapping(target = "resources", ignore = true),
            @Mapping(target = "scheduledAt", ignore = true)
    })

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdatePostRequestDto dto, @MappingTarget Post entity);

    @Mappings({
            @Mapping(target = "id", source = "postId"),
            @Mapping(target = "content", source = "content"),
            @Mapping(target = "publishedAt", source = "publishedAt"),
            @Mapping(target = "likeCount", source = "likeCount"),
            @Mapping(target = "commentCount", source = "commentCount"),
            @Mapping(target = "authorId", source = "authorId")
    })
    PostFeedDto toFeedDto(PostCache postCache);

    PostFeedDto toFeedDto(Post post);
}