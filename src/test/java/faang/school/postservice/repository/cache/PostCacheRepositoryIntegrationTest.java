package faang.school.postservice.repository.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.cache.post.PostCacheDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.repository.cache.post.PostCacheRepositoryImpl;
import faang.school.postservice.util.BaseContextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class PostCacheRepositoryIntegrationTest extends BaseContextTest {

    @Autowired
    private PostCacheRepositoryImpl postCacheRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisProperties redisProperties;
    private PostCacheDto postCacheDto;
    private PostCacheDto postCacheDtoCreated;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        postCacheDto = PostCacheDto.builder()
                .postId(1L)
                .content("This is test")
                .authorId(3L)
                .likesCount(4)
                .build();
        postCacheDtoCreated = PostCacheDto.builder()
                .postId(20L)
                .authorId(40L)
                .likesCount(20)
                .projectId(55L)
                .content("ABCD")
                .comments(List.of(
                        CommentDto.builder()
                                .id(1L)
                                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 1))
                                .build(),
                        CommentDto.builder()
                                .id(2L)
                                .createdAt(LocalDateTime.of(2024, 2, 2, 2, 2))
                                .build(),
                        CommentDto.builder()
                                .id(3L)
                                .createdAt(LocalDateTime.of(2024, 3, 3, 3, 3))
                                .build())
                )
                .build();
        commentDto = CommentDto.builder()
                .id(25L)
                .authorId(20L)
                .content("ABC")
                .createdAt(LocalDateTime.of(2024, 10, 10, 10, 10))
                .build();
    }

    @Test
    @DisplayName("Saving commentCacheDto and then returning it to see if it saved properly")
    public void whenSaveAndFindMethodsCalledThenSerializeAndDeserializeCorrectlyAndStoreInCache() {
        postCacheRepository.save(postCacheDto);
        Optional<PostCacheDto> foundDto = postCacheRepository.findById(postCacheDto.getPostId());
        if (foundDto.isEmpty()) {
            fail();
        }
        long authorId = foundDto.get().getAuthorId();
        assertEquals(authorId, 3);
        long postId = foundDto.get().getPostId();
        assertEquals(postId, 1);
        long likesCount = foundDto.get().getLikesCount();
        assertEquals(likesCount, 4);
        String content = foundDto.get().getContent();
        assertEquals(content, "This is test");
    }

    @Test
    @DisplayName("Testing incrementing like counter by one in existing commentCacheDto in redis")
    public void whenMethodCalledThenIncrementLikeCounterByOne() {
        postCacheRepository.save(postCacheDto);
        boolean isLocked = postCacheRepository.incrementLikesCount(postCacheDto.getPostId());
        assertTrue(isLocked);
        Optional<PostCacheDto> foundDto = postCacheRepository.findById(postCacheDto.getPostId());
        foundDto.ifPresentOrElse(cacheDto -> assertEquals(5, cacheDto.getLikesCount()), Assertions::fail);
    }

    @Test
    @DisplayName("123")
    public void whenThen() {
        postCacheRepository.save(postCacheDto);
        boolean result = postCacheRepository.updatePostsComments(postCacheDto.getPostId(), commentDto);
        assertTrue(result);
        Optional<PostCacheDto> resultDto = postCacheRepository.findById(postCacheDto.getPostId());
        resultDto.ifPresentOrElse(dto ->
                        assertEquals(dto.getComments().get(0).getCreatedAt(), commentDto.getCreatedAt()),
                Assertions::fail);
    }
}
