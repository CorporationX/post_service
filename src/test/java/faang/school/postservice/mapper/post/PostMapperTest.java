package faang.school.postservice.mapper.post;

import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.model.entity.Post;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PostMapperTest {

    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @Test
    void testToPost() {
        PostDto postDto = PostDto.builder()
                .id(1L)
                .content("Test content")
                .authorId(100L)
                .published(true)
                .deleted(false)
                .publishedAt(LocalDateTime.of(2024, 1, 1, 12, 0))
                .createdAt(LocalDateTime.of(2024, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2024, 1, 1, 11, 0))
                .build();

        Post post = postMapper.toPost(postDto);

        assertEquals(postDto.getId(), post.getId());
        assertEquals(postDto.getContent(), post.getContent());
        assertEquals(postDto.getAuthorId(), post.getAuthorId());
        assertEquals(postDto.isPublished(), post.isPublished());
        assertEquals(postDto.isDeleted(), post.isDeleted());
        assertEquals(postDto.getPublishedAt(), post.getPublishedAt());
        assertEquals(postDto.getCreatedAt(), post.getCreatedAt());
        assertEquals(postDto.getUpdatedAt(), post.getUpdatedAt());

        assertNull(post.getLikes());
        assertNull(post.getComments());
    }

    @Test
    void testToPostDto() {
        Post post = Post.builder()
                .id(1L)
                .content("Test content")
                .authorId(100L)
                .published(true)
                .deleted(false)
                .publishedAt(LocalDateTime.of(2024, 1, 1, 12, 0))
                .createdAt(LocalDateTime.of(2024, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2024, 1, 1, 11, 0))
                .build();

        PostDto postDto = postMapper.toPostDto(post);

        assertEquals(post.getId(), postDto.getId());
        assertEquals(post.getContent(), postDto.getContent());
        assertEquals(post.getAuthorId(), postDto.getAuthorId());
        assertEquals(post.isPublished(), postDto.isPublished());
        assertEquals(post.isDeleted(), postDto.isDeleted());
        assertEquals(post.getPublishedAt(), postDto.getPublishedAt());
        assertEquals(post.getCreatedAt(), postDto.getCreatedAt());
        assertEquals(post.getUpdatedAt(), postDto.getUpdatedAt());
    }

    @Test
    void testToPostWithNullDto() {
        Post post = postMapper.toPost(null);

        assertNull(post);
    }

    @Test
    void testToPostDtoWithNullEntity() {
        PostDto postDto = postMapper.toPostDto(null);

        assertNull(postDto);
    }
}
