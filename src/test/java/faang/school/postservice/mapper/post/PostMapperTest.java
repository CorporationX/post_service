package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PostMapperTest {

    private final PostMapperImpl postMapper = new PostMapperImpl();

    private Post post;
    private PostDto postDto;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(1L)
                .content("Some content")
                .authorId(1L)
                .projectId(1L)
                .published(false)
                .deleted(false)
                .build();

        postDto = PostDto.builder()
                .id(1L)
                .content("Some content")
                .authorId(1L)
                .projectId(1L)
                .published(false)
                .deleted(false)
                .build();
    }

    @Test
    void testToEntity() {
        Post mappedPost = postMapper.toEntity(postDto);

        assertEquals(post.getId(), mappedPost.getId());
        assertEquals(post.getContent(), mappedPost.getContent());
        assertEquals(post.getAuthorId(), mappedPost.getAuthorId());
        assertEquals(post.getProjectId(), mappedPost.getProjectId());
        assertEquals(post.isPublished(), mappedPost.isPublished());
        assertEquals(post.isDeleted(), mappedPost.isDeleted());
    }

    @Test
    void testToDto() {
        PostDto mappedDto = postMapper.toDto(post);

        assertEquals(postDto.getId(), mappedDto.getId());
        assertEquals(postDto.getContent(), mappedDto.getContent());
        assertEquals(postDto.getAuthorId(), mappedDto.getAuthorId());
        assertEquals(postDto.getProjectId(), mappedDto.getProjectId());
        assertEquals(postDto.isPublished(), mappedDto.isPublished());
        assertEquals(postDto.isDeleted(), mappedDto.isDeleted());
    }
}