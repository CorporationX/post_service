package faang.school.postservice.mapper;

import faang.school.postservice.cache.model.PostRedis;
import faang.school.postservice.cache.model.UserRedis;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.Ad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PostMapperTest {
    private PostMapper mapper;
    private Post entity;
    private PostDto dto;
    private PostRedis postRedis;

    @BeforeEach
    void setUp() {
        mapper = new PostMapperImpl();
        List<Optional> optionals = getOptionals();
        entity = (Post) optionals.get(0).get();
        dto = (PostDto) optionals.get(1).get();
        postRedis = PostRedis.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .author(UserRedis.builder()
                        .id(entity.getAuthorId())
                        .build())
                .publishedAt(entity.getPublishedAt())
                .likesCount(entity.getLikes().size())
                .views(entity.getViews())
                .build();
    }

    @Test
    void testToDtoFromEntity() {
        PostDto actualDto = mapper.toDto(entity);

        assertEquals(dto, actualDto);
    }

    @Test
    void testToDtoListFromEntityList() {
        List<PostDto> expected = List.of(dto);
        List<Post> posts = List.of(entity);

        List<PostDto> actual = mapper.toDto(posts);

        assertEquals(expected, actual);
    }

    @Test
    void testToEntityFromDto() {
        Post expected = getEntity();

        Post actual = mapper.toEntity(dto);

        assertEquals(expected, actual);
    }

    @Test
    void toRedisFromEntity() {
        PostRedis actual = mapper.toRedis(entity);

        assertEquals(postRedis, actual);
    }

    @Test
    void toRedisListFromEntityList() {
        List<PostRedis> expected = List.of(postRedis);
        List<Post> posts = List.of(entity);

        List<PostRedis> actual = mapper.toRedis(posts);

        assertEquals(expected, actual);
    }

    private Post getEntity() {
        Post post = new Post();
        post.setId(dto.getId());
        post.setContent(dto.getContent());
        post.setAuthorId(dto.getAuthorId());
        post.setProjectId(dto.getProjectId());
        post.setPublished(dto.isPublished());
        post.setPublishedAt(dto.getPublishedAt());
        post.setScheduledAt(dto.getScheduledAt());
        post.setCreatedAt(dto.getCreatedAt());
        post.setUpdatedAt(dto.getUpdatedAt());
        post.setDeleted(dto.isDeleted());

        return post;
    }

    private List<Optional> getOptionals() {
        Long postId = 1L;
        String content = "content";
        Long authorId = ++postId;
        Long projectId = ++postId;
        Like firstLike = new Like();
        Like secondLike = new Like();
        firstLike.setId(++postId);
        secondLike.setId(++postId);
        List<Long> likeIds = List.of(firstLike.getId(), secondLike.getId());
        List<Like> likes = List.of(firstLike, secondLike);
        Comment firstComment = new Comment();
        Comment secondComment = new Comment();
        firstComment.setId(++postId);
        secondComment.setId(++postId);
        List<Comment> comments = List.of(firstComment, secondComment);
        Album firstAlbum = new Album();
        Album secondAlbum = new Album();
        firstAlbum.setId(++postId);
        secondAlbum.setId(++postId);
        List<Album> albums = List.of(firstAlbum, secondAlbum);
        Ad ad = new Ad();
        ad.setId(++postId);
        Resource firstResource = new Resource();
        Resource secondResource = new Resource();
        firstResource.setId(++postId);
        secondResource.setId(++postId);
        List<Resource> resources = List.of(firstResource, secondResource);
        boolean published = false;
        LocalDateTime publishedAt = LocalDateTime.now();
        LocalDateTime scheduledAt = publishedAt.plusDays(1);
        boolean deleted = false;
        LocalDateTime createdAt = publishedAt.minusDays(1);
        LocalDateTime updatedAt = publishedAt.plusMinutes(5);
        long likesCount = likeIds.size();
        long views = 0;

        Optional<Post> postOptional = Optional.of(new Post(postId, content, authorId, projectId, likes, comments,
                albums, ad, resources, published, publishedAt, scheduledAt, deleted, createdAt, updatedAt, views));
        Optional<PostDto> dtoOptional = Optional.of(new PostDto(postId, content, authorId, projectId, published,
                publishedAt, scheduledAt, createdAt, updatedAt, deleted, likesCount, views));

        return List.of(postOptional, dtoOptional);
    }
}
