package faang.school.postservice.service;

import faang.school.postservice.cache.repository.AuthorCacheRepository;
import faang.school.postservice.cache.repository.FeedCacheRepository;
import faang.school.postservice.cache.repository.PostCacheRepository;
import faang.school.postservice.cache.model.author.AuthorCache;
import faang.school.postservice.cache.model.post.PostCache;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.author.AuthorDto;
import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.author.AuthorMapper;
import faang.school.postservice.mapper.feed.FeedMapper;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.feed.FeedServiceImpl;
import faang.school.postservice.service.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedServiceImplTest {

    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @Mock
    private FeedCacheRepository feedCacheRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostCacheRepository postCacheRepository;

    @Mock
    private AuthorCacheRepository authorCacheRepository;

    @Mock
    private UserService userService;

    @Mock
    private AuthorMapper authorMapper;

    @Mock
    private FeedMapper feedMapper;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private FeedServiceImpl feedService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(feedService, "postSize", 20);
    }

    @Test
    void testGetFeedList_WhenRedisHasEnoughPosts_ShouldReturnFromCache() {
        Long userId = 1L;
        Long lastPostId = null;
        List<Long> cachedIds = List.of(102L);

        when(userContext.getUserId()).thenReturn(userId);
        when(feedCacheRepository.get(userId, lastPostId, 20)).thenReturn(cachedIds);

        PostCache postCache = mockPostCache(102L, 10L);
        AuthorCache authorCache = mockAuthorCache(10L);
        FeedPostDto dto = mockFeedPostDto(100L);

        when(postCacheRepository.get(102L)).thenReturn(Optional.of(postCache));
        when(authorCacheRepository.get(10L)).thenReturn(Optional.of(authorCache));
        when(authorMapper.toAuthorDto(authorCache)).thenReturn(mockAuthorDto(10L));
        when(feedMapper.toFeedPostDto(postCache, mockAuthorDto(10L))).thenReturn(dto);

        List<FeedPostDto> result = feedService.getFeedList(lastPostId);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getFeedList_WhenRedisNotEnoughPosts_ShouldFetchFromDb() {
        Long userId = 1L;
        Long lastPostId = null;

        when(userContext.getUserId()).thenReturn(userId);
        when(feedCacheRepository.get(userId, lastPostId, 20)).thenReturn(List.of());

        feedService.getFeedList(lastPostId);

        verify(postRepository).findFeedPosts(
                eq(userId),
                isNull(),
                argThat(pageable -> pageable.getPageSize() == 20)
        );
    }

    private PostCache mockPostCache(Long postId, Long authorId) {
        return new PostCache(
                postId,
                "content",
                authorId,
                LocalDateTime.now(),
                10,
                5
        );
    }

    private AuthorCache mockAuthorCache(Long userId) {
        return new AuthorCache(userId, "username");
    }

    private AuthorDto mockAuthorDto(Long userId) {
        return new AuthorDto(userId, "username");
    }

    private FeedPostDto mockFeedPostDto(Long postId) {
        return FeedPostDto.builder()
                .id(postId)
                .content("content")
                .author(mockAuthorDto(1L))
                .publishedAt(LocalDateTime.now())
                .build();
    }
}