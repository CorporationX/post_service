package faang.school.postservice.service.cache;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.cache.CacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCacheServiceTest {

    @Mock
    private CacheRepository<UserDto> cacheRepository;

    @InjectMocks
    private UserCacheService userCacheService;

    private int timeToLiveCommentAuthor;
    private String userKey;
    private Long userId;
    private UserDto userDto;
    private List<Long> userIds;
    private List<String> userKeys;
    private List<UserDto> expectedUsers;

    @BeforeEach
    void setUp() {
        timeToLiveCommentAuthor = 3;
        userId = 1L;
        userKey = userId + "::user";
        userDto = new UserDto();
        userIds = List.of(1L, 2L, 3L);
        userKeys = userIds.stream()
                .map(id -> id + "::user")
                .toList();
        expectedUsers = userIds.stream()
                .map(id -> UserDto.builder()
                        .id(id)
                        .build())
                .toList();

        ReflectionTestUtils.setField(userCacheService, "timeToLiveCommentAuthor", timeToLiveCommentAuthor);
    }

    @Test
    void save_success() {
        Duration expectedDuration = Duration.ofHours(timeToLiveCommentAuthor);

        userCacheService.save(userId, userDto);

        verify(cacheRepository).set(userKey, userDto, expectedDuration);
    }

    @Test
    void get_success() {
        when(cacheRepository.get(userKey, UserDto.class)).thenReturn(Optional.of(userDto));

        UserDto result = userCacheService.get(userId);

        assertNotNull(result);
        assertEquals(userDto, result);
    }

    @Test
    void get_notFound() {
        when(cacheRepository.get(userKey, UserDto.class)).thenReturn(Optional.empty());

        UserDto result = userCacheService.get(userId);

        assertNull(result);
    }

    @Test
    public void testGetAll_ShouldReturnUsers_WhenCacheHasData() {
        when(cacheRepository.getAll(userKeys, UserDto.class)).thenReturn(Optional.of(expectedUsers));

        List<UserDto> result = userCacheService.getAll(userIds);

        assertNotNull(result);
        assertEquals(expectedUsers, result);
        verify(cacheRepository).getAll(userKeys, UserDto.class);
    }

    @Test
    public void testGetAll_ShouldReturnNull_WhenCacheHasNoData() {
        when(cacheRepository.getAll(userKeys, UserDto.class)).thenReturn(Optional.empty());

        List<UserDto> result = userCacheService.getAll(userIds);

        assertNull(result);
        verify(cacheRepository).getAll(userKeys, UserDto.class);
    }
}
