package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.comment.CommentNewsFeedDto;
import faang.school.postservice.dto.post.CreatePostRequestDto;
import faang.school.postservice.dto.post.FilterPostRequestDto;
import faang.school.postservice.dto.post.PostNewsFeedDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.UpdatePostRequestDto;
import faang.school.postservice.dto.post.serializable.PostCacheDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.album.Album;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    @Mapping(source = "resources", target = "resourceIds", qualifiedByName = "mapResourcesToResourceIds")
    @Mapping(target = "likes", expression = "java(post.getLikes() != null ? post.getLikes().size() : 0)")
    PostResponseDto toDto(Post post);

    List<PostResponseDto> toDtos(List<Post> post);

    Post toEntity(CreatePostRequestDto dto);

    Post toEntity(UpdatePostRequestDto dto);

    Post toEntity(FilterPostRequestDto dto);

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
    @Mapping(target = "likes", expression = "java(post.getLikes() != null ? (long) post.getLikes().size() : 0L)")
    @Mapping(target = "comments", source = "comments", qualifiedByName = "mapCommentsToNewsFeedDtos")
    PostNewsFeedDto toPostNewsFeedDto(Post post);

    @Named("mapCommentsToNewsFeedDtos")
    default List<CommentNewsFeedDto> mapCommentsToNewsFeedDtos(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return Collections.emptyList();
        }
        return comments.stream()
                .map(comment -> new CommentNewsFeedDto(
                        comment.getId(),
                        comment.getAuthorId(),
                        comment.getPost().getId(),
                        comment.getContent(),
                        comment.getLikes() != null
                                ? comment.getLikes().stream().map(Like::getId).toList()
                                : Collections.emptyList(),
                        comment.getCreatedAt()))
                .toList();
    }
}