package faang.school.postservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.redis.RedisService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedServiceTest {

    @Mock
    private PostService postService;

    @Mock
    private UserContext userContext;

    @Spy
    private PostMapperImpl postMapper;

    @Mock
    private RedisService redisService;

    @Spy
    private ObjectMapper objectMapper;

    @InjectMocks
    private FeedService feedService;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(feedService, "defaultNumberPosts", 3L);
    }

    @Test
    void getFeedTest_lastPostIdIsNull() {
        Long userId = 1L;
        Long postId1 = 1L;
        Long postId2 = 2L;
        Long postId3 = 3L;

        ConcurrentLinkedDeque<Long> feedWithPostId = new ConcurrentLinkedDeque<>(List.of(postId1));
        PostResponseDto postResponseDto = createPostResponseDtoWithId(postId1);
        List<Post> postList = List.of(createPostWithId(postId2), createPostWithId(postId3));

        when(userContext.getUserId()).thenReturn(userId);
        when(redisService.getAndDeleteFeed(any())).thenReturn(feedWithPostId);
        when(postService.getPostDtoById(anyLong())).thenReturn(postResponseDto);
        when(postService.getPostByFollowerIdWithLimit(eq(userId), anyLong(), anyLong())).thenReturn(postList);

        List<PostResponseDto> result = feedService.getFeed(null);

        assertEquals(3, result.size());
    }

    @Test
    void getFeedTest_lastPostIdIsNotNull() {
        Long userId = 1L;
        Long lastPostId = 10L;
        Long postId1 = 4L;
        Long postId2 = 5L;
        Long postId3 = 6L;

        ConcurrentLinkedDeque<Long> feedWithPostId = new ConcurrentLinkedDeque<>(List.of(postId1));
        PostResponseDto postResponseDto = createPostResponseDtoWithId(postId1);
        List<Post> postList = List.of(createPostWithId(postId2), createPostWithId(postId3));

        when(userContext.getUserId()).thenReturn(userId);
        when(redisService.getAndDeleteFeed(any())).thenReturn(feedWithPostId);
        when(postService.getPostDtoById(anyLong())).thenReturn(postResponseDto);
        when(postService.getPostByFollowerIdWithLimit(eq(userId), anyLong(), anyLong())).thenReturn(postList);

        List<PostResponseDto> result = feedService.getFeed(lastPostId);

        assertEquals(3, result.size());
    }

    private PostResponseDto createPostResponseDtoWithId(Long id) {
        return PostResponseDto.builder()
                .id(id)
                .build();
    }

    private Post createPostWithId(Long id) {
        return Post.builder()
                .id(id)
                .build();
    }
}
