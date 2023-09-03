package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PostMapperTest {
    @Spy
    private PostMapperImpl postMapper;
    private PostDto postDto;
    private Post post;

    @BeforeEach
    void setUp() {
        postDto = PostDto.builder().id(1L).content("content").authorId(1L).build();
        post = Post.builder().id(1L).content("content").authorId(1L).build();
    }

    @Test
    void testPostToPostDtoShouldMatchAllFields() {
        PostDto actual = postMapper.toDto(post);
        assertAll(
                () -> assertEquals(postDto.getId(), actual.getId()),
                () -> assertEquals(postDto.getContent(), actual.getContent()),
                () -> assertEquals(postDto.getAuthorId(), actual.getAuthorId())
        );
    }

    @Test
    void testPostDtoToPostShouldMatchAllFields() {
        Post actual = postMapper.toPost(postDto);
        assertAll(
                () -> assertEquals(post.getId(), actual.getId()),
                () -> assertEquals(post.getContent(), actual.getContent()),
                () -> assertEquals(post.getAuthorId(), actual.getAuthorId())
        );
    }

    @Test
    void testToDto() {
        PostDto actual = postMapper.toDto(post);
        assertAll(
                () -> assertEquals(postDto.getId(), actual.getId()),
                () -> assertEquals(postDto.getContent(), actual.getContent()),
                () -> assertEquals(postDto.getAuthorId(), actual.getAuthorId())
        );
    }

    @Test
    void testToPost() {
        Post actual = postMapper.toPost(postDto);
        assertAll(
                () -> assertEquals(post.getId(), actual.getId()),
                () -> assertEquals(post.getContent(), actual.getContent()),
                () -> assertEquals(post.getAuthorId(), actual.getAuthorId())
        );
    }
}