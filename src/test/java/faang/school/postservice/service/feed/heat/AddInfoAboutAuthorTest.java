package faang.school.postservice.service.feed.heat;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.cache.RedisCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AddInfoAboutAuthorTest {

    @InjectMocks
    private AddInfoAboutAuthor addInfoAboutAuthor;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private RedisCacheService redisCacheService;

    @Value("${spring.data.redis.directory.infAuthor}")
    private String patternByInfAuthor = "infAuthor:";

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(addInfoAboutAuthor, "patternByInfAuthor", patternByInfAuthor);
    }

    @Test
    public void testAddInfoToRedis() {
        Long userId = 1L;
        Long postId = 1L;

        UserDto userDto = UserDto.builder().id(userId).build();

        when(userServiceClient.getUserByPostId(postId)).thenReturn(userDto);

        addInfoAboutAuthor.addInfoToRedis(userId, postId);

        verify(userServiceClient, times(1)).getUserByPostId(postId);
        verify(redisCacheService, times(1)).saveToCache(patternByInfAuthor, userDto.getId(), userDto);
    }
}
