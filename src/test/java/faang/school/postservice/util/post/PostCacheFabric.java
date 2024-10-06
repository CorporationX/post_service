package faang.school.postservice.util.post;

import faang.school.postservice.dto.post.serializable.PostCacheDto;
import faang.school.postservice.model.album.Album;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@UtilityClass
public class PostCacheFabric {
    private static final int DEFAULT_NUMBER_OF_OBJECT = 3;

    public static Post buildPost(Long id, String content) {
        return Post
                .builder()
                .id(id)
                .content(content)
                .build();
    }

    public static Post buildPost(Long id, boolean published) {
        return Post
                .builder()
                .id(id)
                .published(published)
                .build();
    }

    public static Post buildPost(Long id, String content, Long authorId) {
        return Post
                .builder()
                .id(id)
                .content(content)
                .authorId(authorId)
                .build();
    }

    public static Post buildPost(Long id, String content, Long authorId, List<String> hashTags) {
        return Post
                .builder()
                .id(id)
                .content(content)
                .authorId(authorId)
                .hashTags(hashTags)
                .build();
    }

    public static Post buildPost(String content, List<String> hashTags) {
        return Post
                .builder()
                .content(content)
                .hashTags(hashTags)
                .build();
    }

    public static Post buildPost(Long id, String content, boolean deleted, boolean published,
                                 Long authorId, Long projectId,
                                 LocalDateTime createdAt, LocalDateTime publishedAt) {
        return Post
                .builder()
                .id(id)
                .content(content)
                .deleted(deleted)
                .publishedAt(publishedAt)
                .authorId(authorId)
                .projectId(projectId)
                .createdAt(createdAt)
                .publishedAt(publishedAt)
                .build();
    }

    public static PostCacheDto buildPostCacheDto() {
        return PostCacheDto
                .builder()
                .build();
    }

    public static PostCacheDto buildPostCacheDto(Long id) {
        return PostCacheDto
                .builder()
                .id(id)
                .build();
    }

    public static PostCacheDto buildPostCacheDto(Long id, LocalDateTime publishedAt) {
        return PostCacheDto
                .builder()
                .id(id)
                .publishedAt(publishedAt)
                .build();
    }

    public static List<PostCacheDto> buildPostCacheDtosWithTags(int number) {
        return LongStream
                .rangeClosed(1, number)
                .mapToObj(i -> buildPostCacheDtoWithTags(i, buildHashTags(DEFAULT_NUMBER_OF_OBJECT)))
                .toList();
    }

    public static PostCacheDto buildPostCacheDtoWithTags(Long id, List<String> tags) {
        return PostCacheDto
                .builder()
                .id(id)
                .hashTags(tags)
                .build();
    }

    public static List<String> buildHashTags(int number) {
        return IntStream
                .rangeClosed(1, number)
                .mapToObj(i -> "tag" + i)
                .toList();
    }

    public static List<PostCacheDto> buildPostCacheDtosForMapping() {
        return IntStream
                .range(0, DEFAULT_NUMBER_OF_OBJECT)
                .mapToObj(i -> buildPostCacheDtoForTestMapToPostCacheDto())
                .toList();
    }

    public static PostCacheDto buildPostCacheDtoForTestMapToPostCacheDto() {
        return PostCacheDto
                .builder()
                .likesIds(getListOfIds(DEFAULT_NUMBER_OF_OBJECT))
                .commentIds(getListOfIds(DEFAULT_NUMBER_OF_OBJECT))
                .albumIds(getListOfIds(DEFAULT_NUMBER_OF_OBJECT))
                .resourceIds(getListOfIds(DEFAULT_NUMBER_OF_OBJECT))
                .build();
    }

    public static List<Long> getListOfIds(int numberOfIds) {
        List<Long> ids = new ArrayList<>();
        LongStream.rangeClosed(1, numberOfIds)
                .forEach(ids::add);
        return ids;
    }

    public static List<Post> buildPostsForMapping() {
        return IntStream
                .range(0, DEFAULT_NUMBER_OF_OBJECT)
                .mapToObj(i -> buildPostForTestMapToPostCacheDto())
                .toList();
    }

    public static Post buildPostForTestMapToPostCacheDto() {
        return Post
                .builder()
                .likes(buildLikes(DEFAULT_NUMBER_OF_OBJECT))
                .comments(buildComments(DEFAULT_NUMBER_OF_OBJECT))
                .albums(buildAlbums(DEFAULT_NUMBER_OF_OBJECT))
                .resources(buildResource(DEFAULT_NUMBER_OF_OBJECT))
                .build();
    }

    public static List<Like> buildLikes(int numberOfLikes) {
        return LongStream
                .rangeClosed(1, numberOfLikes)
                .mapToObj(PostCacheFabric::buildLike)
                .toList();
    }

    public static Like buildLike(Long id) {
        return Like
                .builder()
                .id(id)
                .build();
    }

    public static List<Comment> buildComments(int numberOfComments) {
        return LongStream
                .rangeClosed(1, numberOfComments)
                .mapToObj(PostCacheFabric::buildComment)
                .toList();
    }

    public static Comment buildComment(Long id) {
        return Comment
                .builder()
                .id(id)
                .build();
    }

    public static List<Album> buildAlbums(int numberOfAlbums) {
        return LongStream
                .rangeClosed(1, numberOfAlbums)
                .mapToObj(PostCacheFabric::buildAlbum)
                .toList();
    }

    public static Album buildAlbum(Long id) {
        return Album
                .builder()
                .id(id)
                .build();
    }

    public static List<Resource> buildResource(int numberOfResource) {
        return LongStream
                .rangeClosed(1, numberOfResource)
                .mapToObj(PostCacheFabric::buildResource)
                .toList();
    }

    public static Resource buildResource(Long id) {
        return Resource
                .builder()
                .id(id)
                .build();
    }
}
