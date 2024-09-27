package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PostMapperTest {
    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);
    private Post post;
    private PostDto postDto;
    private final long ANY_ID = 1L;
    private final String CONTENT = "Content";

    @BeforeEach
    public void init() {
        post = Post.builder()
                .id(ANY_ID)
                .content(CONTENT)
                .authorId(ANY_ID)
                .projectId(ANY_ID)
                .published(true)
                .publishedAt(LocalDateTime.now())
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .build();
        postDto = PostDto.builder()
                .id(ANY_ID)
                .content(CONTENT)
                .authorId(ANY_ID)
                .projectId(ANY_ID)
                .published(true)
                .publishedAt(LocalDateTime.now())
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Testing mapping entity to dto")
    void whenEntityMappedToDtoThenSuccess() {
        PostDto postDtoResult = postMapper.toDto(post);

        assertNotNull(postDtoResult);
        assertEquals(post.getId(), postDtoResult.getId());
        assertEquals(post.getContent(), postDtoResult.getContent());
        assertEquals(post.getAuthorId(), postDtoResult.getAuthorId());
        assertEquals(post.getProjectId(), postDtoResult.getProjectId());
        assertEquals(post.isPublished(), postDtoResult.isPublished());
        assertEquals(post.getPublishedAt(), postDtoResult.getPublishedAt());
        assertEquals(post.isDeleted(), postDtoResult.isDeleted());
        assertEquals(post.getCreatedAt(), postDtoResult.getCreatedAt());
    }

    @Test
    @DisplayName("Testing mapping dto to entity")
    void whenDtoMappedToEntityThenSuccess() {
        Post postResult = postMapper.toEntity(postDto);

        assertNotNull(postResult);
        assertEquals(postDto.getId(), postResult.getId());
        assertEquals(postDto.getContent(), postResult.getContent());
        assertEquals(postDto.getAuthorId(), postResult.getAuthorId());
        assertEquals(postDto.getProjectId(), postResult.getProjectId());
        assertEquals(postDto.isPublished(), postResult.isPublished());
        assertEquals(postDto.getPublishedAt(), postResult.getPublishedAt());
        assertEquals(postDto.isDeleted(), postResult.isDeleted());
        assertEquals(postDto.getCreatedAt(), postResult.getCreatedAt());
    }
}