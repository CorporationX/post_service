package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.RedisPostDto;
import faang.school.postservice.model.album.Album;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.ad.Ad;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    List<PostDto> toDtoList(List<Post> posts);

    Post toEntity(CreatePostDto createPostDto);

    RedisPostDto toRedisPostDto(PostDto postDto);

    @Mapping(target = "likesId", source = "likes", qualifiedByName = "toLikesId")
    @Mapping(target = "commentsId", source = "comments", qualifiedByName = "toCommentsId")
    @Mapping(target = "albumsId", source = "albums", qualifiedByName = "toAlbumsId")
    @Mapping(target = "adId", source = "ad.id")
    PostDto toDto(Post post);

    @Mapping(target = "likes", source = "likesId", qualifiedByName = "toLikes")
    @Mapping(target = "comments", source = "commentsId", qualifiedByName = "toComments")
    @Mapping(target = "albums", source = "albumsId", qualifiedByName = "toAlbums")
    @Mapping(target = "ad", source = "adId", qualifiedByName = "toAd")
    Post toEntity(PostDto postDto);

    @Named(value = "toAd")
    default Ad toAd(Long ad) {
        return Ad.builder().id(ad).build();
    }

    @Named(value = "toAlbumsId")
    default List<Long> toAlbumsId(List<Album> albums) {
        if (albums == null) {
            return new ArrayList<>();
        }
        return albums.stream().map(Album::getId).toList();
    }

    @Named(value = "toAlbums")
    default List<Album> toAlbums(List<Long> albumsId) {
        if (albumsId == null) {
            return new ArrayList<>();
        }
        List<Album> albums = new ArrayList<>();
        for (Long id : albumsId) {
            albums.add(Album.builder().id(id).build());
        }
        return albums;
    }

    @Named(value = "toCommentsId")
    default List<Long> toCommentsId(List<Comment> comments) {
        if (comments == null) {
            return new ArrayList<>();
        }
        return comments.stream().map(Comment::getId).toList();
    }

    @Named(value = "toComments")
    default List<Comment> toComments(List<Long> commentsId) {
        if (commentsId == null) {
            return new ArrayList<>();
        }
        List<Comment> comments = new ArrayList<>();
        for (Long id : commentsId) {
            comments.add(Comment.builder().id(id).build());
        }
        return comments;
    }

    @Named(value = "toLikesId")
    default List<Long> toLikesId(List<Like> likes) {
        if (likes == null) {
            return new ArrayList<>();
        }
        return likes.stream().map(Like::getId).toList();
    }

    @Named(value = "toLikes")
    default List<Like> toLikes(List<Long> likesId) {
        if (likesId == null) {
            return new ArrayList<>();
        }
        List<Like> likes = new ArrayList<>();
        for (Long id : likesId) {
            likes.add(Like.builder().id(id).build());
        }
        return likes;
    }
}
