package faang.school.postservice.mapper;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.PostResponseDto;
import faang.school.postservice.dto.feed.PostFeedResponse;
import faang.school.postservice.dto.feed.PostPublishEvent;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "ad", ignore = true)
    @Mapping(target = "resources", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Post toEntity(PostDto postDto);

    @Mapping(target = "likeCount", source = "likes", qualifiedByName = "calculateLikeCount")
    @Mapping(target = "commentsId", expression = "java(mapCommentToIds(post.getComments()))")
    @Mapping(target = "albumsId", expression = "java(mapAlbumToIds(post.getAlbums()))")
    @Mapping(source = "ad.id", target = "adId")
    @Mapping(target = "resourcesId", expression = "java(mapResourceToIds(post.getResources()))")
    @Mapping(target = "hashtagsId", ignore = true)
    PostResponseDto toResponseDto(Post post);

    PostRedisDto toRedisDto(PostResponseDto postResponseDto);

    @Mapping(target = "postId", source = "id")
    PostPublishEvent toPublishEvent(PostRedisDto postRedisDto);

    List<PostResponseDto> toResponseDtoList(List<Post> posts);

    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "author", ignore = true)
    PostFeedResponse responseToFeedResponse(PostResponseDto postResponseDto);

    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "author", ignore = true)
    PostFeedResponse redisEventToFeedResponse(PostRedisDto postRedisDto);

    List<PostRedisDto> toRedisDtoList(List<PostResponseDto> postResponseDtoList);

    default List<Long> mapCommentToIds(List<Comment> comments) {
        return comments != null ? comments.stream()
                .map(Comment::getId)
                .toList() : Collections.emptyList();
    }

    default List<Long> mapAlbumToIds(List<Album> albums) {
        return albums != null ? albums.stream()
                .map(Album::getId)
                .toList() : Collections.emptyList();
    }

    default List<Long> mapResourceToIds(List<Resource> resources) {
        return resources != null ? resources.stream()
                .map(Resource::getId)
                .toList() : Collections.emptyList();
    }

    @Named("calculateLikeCount")
    default Integer calculateLikeCount(List<Like> likes) {
        return likes != null ? likes.size() : 0;
    }
}
