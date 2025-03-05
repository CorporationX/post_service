package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.feed.PostDTO;
import faang.school.postservice.dto.post.PostDraftCreateDto;
import faang.school.postservice.dto.post.PostDraftResponseDto;
import faang.school.postservice.dto.post.PostDraftWithFilesCreateDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.redis.entities.PostCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PostMapper {

    PostResponseDto toDto(Post post);

    Post toEntity(PostResponseDto postDto);

    Post toEntityFromDraftDto(PostDraftCreateDto dto);

    Post toEntityFromDraftDtoWithFiles(PostDraftWithFilesCreateDto dto);

    @Mapping(source = "albums", target = "albumsIds", qualifiedByName = "mapAlbumsToIds")
    @Mapping(source = "resources", target = "resourcesIds", qualifiedByName = "mapResourcesToIds")
    PostDraftResponseDto toDraftDtoFromPost(Post post);

    @Mapping(source = "albums", target = "albumsIds", qualifiedByName = "mapAlbumsToIds")
    @Mapping(source = "resources", target = "resourcesIds", qualifiedByName = "mapResourcesToIds")
    PostResponseDto toDtoFromPost(Post post);

    @Named("mapAlbumsToIds")
    default List<Long> mapAlbumsToIds(List<Album> albums) {
        return albums.stream().map(Album::getId).toList();
    }

    @Named("mapResourcesToIds")
    default List<Long> mapResourcesToIds(List<Resource> resources) {
        return resources.stream().map(Resource::getId).toList();
    }

    PostDTO postCacheToPostDTO(PostCache postCache);

    @Mapping(target = "visibility", expression = "java(convertVisibility(post))")
    PostCache toPostCache(Post post);

    default faang.school.postservice.dto.post.PostVisibility convertVisibility(Post post) {
        return post.isVisible() ? faang.school.postservice.dto.post.PostVisibility.PUBLIC : faang.school.postservice.dto.post.PostVisibility.PRIVATE;
    }
}