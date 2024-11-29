package faang.school.postservice.service.redis;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.redis.CachedAuthorMapper;
import faang.school.postservice.model.redis.CachedAuthor;
import faang.school.postservice.repository.redis.CachedAuthorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CachedAuthorServiceTest {
    @Mock
    private CachedAuthorRepository repository;
    @Mock
    private CachedAuthorMapper cachedAuthorMapper;
    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private CachedAuthorService cachedAuthorService;

    @Test
    public void testSaveAuthorCache() {
        Long userId = 1L;
        UserDto userDto = new UserDto(userId, "John Doe", "john.doe@example.com");
        CachedAuthor cachedAuthor = new CachedAuthor(userId, "John Doe", "john.doe@example.com");

        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(cachedAuthorMapper.toCachedAuthor(userDto)).thenReturn(cachedAuthor);
        cachedAuthorService.saveAuthorCache(userId);

        verify(repository).save(cachedAuthor);
    }
}