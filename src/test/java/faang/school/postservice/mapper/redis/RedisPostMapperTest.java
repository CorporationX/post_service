package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.PostPair;
import faang.school.postservice.dto.redis.RedisCommentDto;
import faang.school.postservice.dto.redis.RedisPostDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisPost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RedisPostMapperTest {

    private RedisCommentMapper redisCommentMapper = new RedisCommentMapperImpl();

    private RedisPostMapper redisPostMapper = new RedisPostMapperImpl(redisCommentMapper);

    private Post post;

    private RedisPost redisPost;

    private RedisCommentDto firstRedisCommentDto;
    private RedisCommentDto secondRedisCommentDto;

    private List<Like> likes;
    private List<Comment> comments;
    private List<RedisCommentDto> redisCommentDtos;

    private Like firstLike;
    private Like secondLike;

    private Comment firstComment;
    private Comment secondComment;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;

    private final Long postId = 1L;
    private final Long authorId = 1L;
    private final String content = "Content";

    @BeforeEach
    void setUp() {
        createdAt = LocalDateTime.now().minusMonths(6);
        updatedAt = LocalDateTime.now().minusDays(2);
        publishedAt = LocalDateTime.now().minusMonths(5);
        firstComment = Comment.builder()
                .id(1L)
                .authorId(authorId)
                .content(content)
                .createdAt(createdAt)
                .build();
        secondComment = Comment.builder()
                .id(2L)
                .authorId(authorId)
                .content(content)
                .createdAt(createdAt)
                .build();
        firstRedisCommentDto = RedisCommentDto.builder()
                .id(1L)
                .authorId(authorId)
                .content(content)
                .createdAt(createdAt)
                .build();
        secondRedisCommentDto = RedisCommentDto.builder()
                .id(2L)
                .authorId(authorId)
                .content(content)
                .createdAt(createdAt)
                .build();
        firstLike = Like.builder()
                .id(1L)
                .build();
        secondLike = Like.builder()
                .id(2L)
                .build();
        likes = new ArrayList<>(List.of(firstLike, secondLike));
        comments = new ArrayList<>(List.of(firstComment, secondComment));
        redisCommentDtos = new ArrayList<>(List.of(firstRedisCommentDto, secondRedisCommentDto));
        post = Post.builder()
                .id(postId)
                .content(content)
                .authorId(authorId)
                .likes(likes)
                .comments(comments)
                .published(true)
                .publishedAt(publishedAt)
                .updatedAt(updatedAt)
                .build();
        redisPost = RedisPost.builder()
                .postId(postId)
                .content(content)
                .authorId(authorId)
                .commentsDto(redisCommentDtos)
                .postViews(0L)
                .postLikes(2L)
                .publishedAt(publishedAt)
                .updatedAt(updatedAt)
                .build();
    }

    @Test
    void toRedisPostTest() {
        RedisPost result = redisPostMapper.toRedisPost(post);
        assertEquals(redisPost, result);
    }

    @Test
    void toRedisPostDtoTest() {
        RedisPostDto expected = RedisPostDto.builder()
                .postId(postId)
                .content(content)
                .postViews(0L)
                .postLikes(2L)
                .comments(redisCommentDtos)
                .publishedAt(publishedAt)
                .updatedAt(updatedAt)
                .build();

        RedisPostDto result = redisPostMapper.toRedisPostDto(redisPost);

        assertEquals(expected, result);
    }

    @Test
    void toPostPairTest() {
        PostPair expected = PostPair.builder()
                .postId(postId)
                .publishedAt(publishedAt)
                .build();

        PostPair result = redisPostMapper.toPostPair(redisPost);

        assertEquals(expected, result);
    }
}