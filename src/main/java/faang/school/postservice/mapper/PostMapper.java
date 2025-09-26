package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostOutputDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.redis.CachedPost;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;


@Mapper(componentModel = "Spring", uses = {LikeEventMapper.class, CommentMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    @Mapping(target = "likeIds", source = "likes", qualifiedByName = "mapLikes")
    @Mapping(target = "commentIds", source = "comments", qualifiedByName = "mapComments")
    @Mapping(target = "albumIds", source = "albums", qualifiedByName = "mapAlbums")
    @Mapping(target = "adId", source = "ad.id")
    @Mapping(target = "resourceIds", source = "resources", qualifiedByName = "mapResources")
    PostOutputDto toPostDto(Post post);

    Post toPostEntity(PostCreateDto postCreateDto);

    void update(PostUpdateDto postUpdateDto, @MappingTarget Post post);

    CachedPost toCachedPost(Post post);

    @Named("mapLikes")
    default List<Long> mapLikesToIds(List<Like> likes) {
        if (likes == null) {
            return null;
        }
        return likes.stream()
                .map(Like::getId)
                .toList();
    }

    @Named("mapComments")
    default List<Long> mapCommentsToIds(List<Comment> comments) {
        if (comments == null) {
            return null;
        }
        return comments.stream()
                .map(Comment::getId)
                .toList();
    }

    @Named("mapAlbums")
    default List<Long> mapAlbumsToIds(List<Album> albums) {
        if (albums == null) {
            return null;
        }
        return albums.stream()
                .map(Album::getId)
                .toList();
    }

    @Named("mapResources")
    default List<Long> mapResourcesToIds(List<Resource> resources) {
        if (resources == null) {
            return null;
        }
        return resources.stream()
                .map(Resource::getId)
                .toList();
    }
}
