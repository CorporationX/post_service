package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.redisCache.PostCache;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(source = "likes", target = "likeIds", qualifiedByName = "mapLikes")
    @Mapping(source = "comments", target = "commentIds", qualifiedByName = "mapComments")
    @Mapping(source = "albums", target = "albumIds", qualifiedByName = "mapAlbums")
    @Mapping(source = "ad.id", target = "adId")
    @Mapping(source = "resources", target = "resourceIds", qualifiedByName = "mapResources")
    @Mapping(target = "numberLikes", expression = "java(mapLikesToNumLikes(entity.getLikes()))")
    PostDto toDto(Post entity);

    PostDto toDto(PostCache postCache);

    Post toEntity(PostDto dto);

    PostCache toPostCache(Post entity);

    @Named("mapLikes")
    default List<Long> mapLikesToLikeIds(List<Like> likes) {
        if (likes == null) {
            likes = new ArrayList<>();
        }
        return likes.stream()
                .map(Like::getId)
                .toList();
    }

    @Named("mapComments")
    default List<Long> mapCommentsToCommentIds(List<Comment> comments) {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        return comments.stream()
                .map(Comment::getId)
                .toList();
    }

    @Named("mapAlbums")
    default List<Long> mapAlbumsToAlbumsIds(List<Album> albums) {
        if (albums == null) {
            albums = new ArrayList<>();
        }
        return albums.stream()
                .map(Album::getId)
                .toList();
    }

    @Named("mapResources")
    default List<Long> mapResourcesToResourceIds(List<Resource> resources) {
        if (resources == null) {
            resources = new ArrayList<>();
        }
        return resources.stream()
                .map(Resource::getId)
                .toList();
    }

    default Long mapLikesToNumLikes(List<Like> likes) {
        if (likes == null) return 0L;
        return ((long) likes.size());
    }
}