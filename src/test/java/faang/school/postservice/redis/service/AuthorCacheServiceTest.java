package faang.school.postservice.redis.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.redis.mapper.AuthorCacheMapper;
import faang.school.postservice.redis.model.AuthorCache;
import faang.school.postservice.redis.repository.AuthorCacheRedisRepository;
import faang.school.postservice.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;

import static faang.school.postservice.util.TestDataFactory.POST_AUTHOR_ID;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorCacheServiceTest {
    @InjectMocks
    private AuthorCacheService authorCacheService;
    @Mock
    private AuthorCacheRedisRepository repository;
    @Mock
    private AuthorCacheMapper authorCacheMapper;
    @Mock
    private UserServiceClient userServiceClient;
    @Test
    void givenUsersWhenSaveAllAuthorsInCacheThenAllUsersSavedInCache() throws ExecutionException, InterruptedException {
        var allUsers = TestDataFactory.createUserDtoList();

        var authorCacheList = allUsers.stream()
                .map(user -> new AuthorCache())
                .toList();

        when(authorCacheMapper.toAuthorCache(any(UserDto.class)))
                    .thenAnswer(invocation -> new AuthorCache());

        // when - action
        authorCacheService.saveAllAuthorsInCache(allUsers).get();

        // then - verify the output
        verify(authorCacheMapper, times(allUsers.size())).toAuthorCache(any(UserDto.class));
        verify(repository, times(1)).saveAll(authorCacheList);
    }

    @Test
    void givenPostAuthorIdWhenSaveAuthorCacheThenAuthorIsSaved() {
        var userDto = TestDataFactory.createUserDto();
        var authorCache = TestDataFactory.createAuthorCache();

        when(userServiceClient.getUser(POST_AUTHOR_ID)).thenReturn(userDto);
        when(authorCacheMapper.toAuthorCache(userDto)).thenReturn(authorCache);

        // when - action
        authorCacheService.saveAuthorCache(POST_AUTHOR_ID);

        // then - verify the output
        verify(authorCacheMapper, times(1)).toAuthorCache(userDto);
        verify(repository, times(1)).save(authorCache);
    }
}