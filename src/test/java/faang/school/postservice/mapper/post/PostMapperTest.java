package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.ad.Ad;
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

    @BeforeEach
    void setUp() {
        mapper = new PostMapperImpl();
        List<Optional> optionals = getOptionals();
        entity = (Post) optionals.get(0).get();
        dto = (PostDto) optionals.get(1).get();
    }

    @Test
    void testToDto() {
        // when
        PostDto actualDto = mapper.toDto(entity);

        // then
        assertEquals(dto, actualDto);
    }

    @Test
    void testToEntity() {
        // given
        Post expEntity = getEntity();
        // when
        Post actualEntity = mapper.toEntity(dto);
        // then
        assertEquals(expEntity, actualEntity);
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
        List<Long> commentIds = List.of(firstComment.getId(), secondComment.getId());
        List<Comment> comments = List.of(firstComment, secondComment);
        Album firstAlbum = new Album();
        Album secondAlbum = new Album();
        firstAlbum.setId(++postId);
        secondAlbum.setId(++postId);
        List<Long> albumIds = List.of(firstAlbum.getId(), secondAlbum.getId());
        List<Album> albums = List.of(firstAlbum, secondAlbum);
        Ad ad = new Ad();
        ad.setId(++postId);
        Resource firstResource = new Resource();
        Resource secondResource = new Resource();
        firstResource.setId(++postId);
        secondResource.setId(++postId);
        List<Long> resourceIds = List.of(firstResource.getId(), secondResource.getId());
        List<Resource> resources = List.of(firstResource, secondResource);
        boolean published = false;
        LocalDateTime publishedAt = LocalDateTime.now();
        LocalDateTime scheduledAt = publishedAt.plusDays(1);
        boolean deleted = false;
        LocalDateTime createdAt = publishedAt.minusDays(1);
        LocalDateTime updatedAt = publishedAt.plusMinutes(5);

        Optional<Post> postOptional = Optional.of(new Post(postId, content, authorId, projectId, likes, comments,
                albums, ad, resources, published, publishedAt, scheduledAt, deleted, createdAt, updatedAt));
        Optional<PostDto> dtoOptional = Optional.of(new PostDto(postId, content, authorId, projectId, likeIds, commentIds,
                albumIds, ad.getId(), resourceIds, published, publishedAt, scheduledAt, deleted));

        return List.of(postOptional, dtoOptional);
    }
}
