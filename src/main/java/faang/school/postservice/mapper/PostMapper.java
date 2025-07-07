package faang.school.postservice.mapper;


import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.PostFeedResponseDto;
import faang.school.postservice.dto.PostResponseDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.entity.CachedPost;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "albums", ignore = true)
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

    default CachedPost toCachedPost(Post post) {
        return CachedPost.builder()
                .id(post.getId())
                .content(post.getContent())
                .authorId(post.getAuthorId())
                .projectId(post.getProjectId())
                .publishedAt(Instant.from(post.getPublishedAt()))
                .comments(toCommentDto(post.getComments()))
                .likes(post.getLikes().size())
                .build();
    }

    default List<CommentDto> toCommentDto(List<Comment> comments) {
        if (comments == null) {
            return List.of();
        }
        return comments.stream()
                .map(comment ->
                        CommentDto.builder()
                                .postId(comment.getPost().getId())
                                .id(comment.getId())
                                .authorId(comment.getAuthorId())
                                .content(comment.getContent())
                                .createdAt(comment.getCreatedAt())
                                .build())
                .toList();

    }


    List<PostResponseDto> toResponseDtoList(List<Post> posts);

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

    default PostFeedResponseDto toPostFeedResponseDto(CachedPost cachedPost) {
        return new PostFeedResponseDto(
                cachedPost.getId(),
                cachedPost.getAuthorId(),
                cachedPost.getContent(),
                cachedPost.getProjectId(),
                cachedPost.getPublishedAt(),
                cachedPost.getLikes(),
                cachedPost.getComments()
        );


    }

}
