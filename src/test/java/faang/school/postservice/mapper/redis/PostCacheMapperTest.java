package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.redis.cash.CommentCache;
import faang.school.postservice.dto.redis.cash.PostCache;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PostCacheMapperTest {

    @Spy
    private PostCacheMapperImpl postCacheMapper;

    @Spy
    private CommentCacheMapperImpl commentCacheMapper;

    private Post post;
    private PostCache postCache;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(postCacheMapper, "commentCacheMapper", commentCacheMapper);

        post = Post.builder()
                .id(1L)
                .content("content")
                .authorId(1L)
                .comments(List.of(Comment.builder()
                        .id(1L)
                        .authorId(1L)
                        .post(Post.builder().id(1L).build())
                        .content("Lol!")
                        .createdAt(LocalDateTime.of(2020, 1, 1, 1, 1))
                        .updatedAt(LocalDateTime.of(2020, 1, 1, 1, 1))
                        .build()))
                .likes(List.of(Like.builder().id(1L).build()))
                .publishedAt(LocalDateTime.of(2020, 1, 1, 1, 1))
                .build();

        postCache = PostCache.builder()
                .id("1")
                .content("content")
                .authorId(1L)
                .comments(List.of(CommentCache.builder()
                        .id(1L)
                        .authorId(1L)
                        .postId(1)
                        .content("Lol!")
                        .createdAt(LocalDateTime.of(2020, 1, 1, 1, 1))
                        .updatedAt(LocalDateTime.of(2020, 1, 1, 1, 1))
                        .build()))
                .likes(List.of(1L))
                .publishedAt(LocalDateTime.of(2020, 1, 1, 1, 1))
                .build();
    }

    @Test
    void testToDto() {
        PostCache actual = postCacheMapper.toDto(post);

        assertEquals(postCache, actual);
    }

    @Test
    void testToEntity() {
        Post actual = postCacheMapper.toEntity(postCache);

        assertEquals(post, actual);
    }
}