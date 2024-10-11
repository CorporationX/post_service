package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.CreatePostRequestDto;
import faang.school.postservice.dto.post.FilterPostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.UpdatePostRequestDto;
import faang.school.postservice.dto.post.serializable.PostCacheDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.album.Album;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    @Mapping(source = "resources", target = "resourceIds", qualifiedByName = "mapResourcesToResourceIds")
    PostResponseDto toDto(Post post);

    Post toEntity(CreatePostRequestDto dto);

    Post toEntity(UpdatePostRequestDto dto);

    Post toEntity(FilterPostRequestDto dto);

    List<PostResponseDto> toDtos(List<Post> posts);

    @Named("mapResourcesToResourceIds")
    default List<Long> mapResourcesToResourceIds(List<Resource> resources) {
        if (resources == null) {
            return new ArrayList<>();
        }
        return resources.stream()
                .map(Resource::getId)
                .toList();
    }

    List<PostResponseDto> listEntitiesToListDto(List<Post> posts);

    PostResponseDto toDto(PostCacheDto postCacheDto);

    List<PostResponseDto> postCacheDtoToPostResponseDto(List<PostCacheDto> postCacheDtos);

    @Mapping(source = "likes", target = "likesIds", qualifiedByName = "mapLikes")
    @Mapping(source = "comments", target = "commentIds", qualifiedByName = "mapComments")
    @Mapping(source = "albums", target = "albumIds", qualifiedByName = "mapAlbums")
    @Mapping(source = "resources", target = "resourceIds", qualifiedByName = "mapResources")
    PostCacheDto toPostCacheDto(Post post);

    List<PostCacheDto> mapToPostCacheDtos(List<Post> posts);

    @Named("mapLikes")
    default List<Long> mapLikes(List<Like> likes) {
        return likes
                .stream()
                .map(Like::getId)
                .toList();
    }

    @Named("mapComments")
    default List<Long> mapComments(List<Comment> comments) {
        return comments
                .stream()
                .map(Comment::getId)
                .toList();
    }

    @Named("mapAlbums")
    default List<Long> mapAlbums(List<Album> albums) {
        return albums
                .stream()
                .map(Album::getId)
                .toList();
    }

    @Named("mapResources")
    default List<Long> mapResources(List<Resource> resources) {
        return resources
                .stream()
                .map(Resource::getId)
                .toList();
    }
}