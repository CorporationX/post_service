package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.cache.MultiGetCacheService;
import faang.school.postservice.service.cache.SingleCacheService;
import faang.school.postservice.util.CollectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {


    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private SingleCacheService<Long, UserDto> userCacheService;

    @Mock
    private MultiGetCacheService<List<Long>, UserDto> userMultiGetCacheService;

    @Mock
    private CollectionUtils collectionUtils;

    @InjectMocks
    private UserServiceImpl userService;

    private Long userId;
    private UserDto cachedUser;
    private UserDto userFromService;
    private List<Long> userIds;
    private List<UserDto> cachedUsers;
    private List<UserDto> missingUsers;
    private List<Long> missingUserIds;

    @BeforeEach
    void setUp() {
        userId = 1L;
        cachedUser = UserDto.builder()
                .id(userId)
                .username("Cached User")
                .build();
        userFromService = UserDto.builder()
                .id(userId)
                .username("User From Service")
                .build();
        userIds = List.of(1L, 2L, 3L);
        cachedUsers = Arrays.asList(
                UserDto.builder().id(1L).username("User 1").build(),
                null,
                UserDto.builder().id(3L).username("User 3").build()
        );
        missingUsers = List.of(
                UserDto.builder().id(2L).username("User 2").build()
        );
        missingUserIds = List.of(2L);
    }

    @Test
    void testGetUserFromCacheOrService_CacheHit() {
        when(userCacheService.get(userId)).thenReturn(cachedUser);

        UserDto result = userService.getUserFromCacheOrService(userId);

        assertNotNull(result);
        assertEquals(cachedUser, result);
        verify(userServiceClient, never()).getUser(userId);
    }

    @Test
    void testGetUserFromCacheOrService_CacheMiss() {
        when(userCacheService.get(userId)).thenReturn(null);
        when(userServiceClient.getUser(userId)).thenReturn(userFromService);

        UserDto result = userService.getUserFromCacheOrService(userId);

        assertNotNull(result);
        assertEquals(userFromService, result);
        verify(userServiceClient).getUser(userId);
    }

    @Test
    void testGetUsersFromCacheOrService_CacheHit_AllUsers() {
        when(userMultiGetCacheService.getAll(userIds)).thenReturn(cachedUsers);
        when(userServiceClient.getUsersByIds(missingUserIds)).thenReturn(missingUsers);
        doAnswer(invocationOnMock -> {
            cachedUsers.set(1, missingUsers.get(0));
            return null;
        }).when(collectionUtils).replaceNullsWith(cachedUsers, missingUsers);

        List<UserDto> result = userService.getUsersFromCacheOrService(userIds);

        assertEquals(3, result.size());
        assertNotNull(result.get(0));
        assertNotNull(result.get(1));
        assertNotNull(result.get(2));
        verify(userServiceClient).getUsersByIds(missingUserIds);
    }
}