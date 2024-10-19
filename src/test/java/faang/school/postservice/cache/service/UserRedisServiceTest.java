package faang.school.postservice.cache.service;

import faang.school.postservice.cache.model.UserRedis;
import faang.school.postservice.cache.repository.UserRedisRepository;
import faang.school.postservice.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRedisServiceTest {
    @InjectMocks
    private UserRedisService userRedisService;
    @Mock
    private UserRedisRepository userRedisRepository;

    private Iterable<UserRedis> userRedisIterable;
    private UserDto userDto;
    private Long id;

    @BeforeEach
    void setUp() {
        id = 1L;
        userRedisIterable = List.of(
                new UserRedis(1L, "username"),
                new UserRedis(2L, "username")
        );
        userDto = UserDto.builder()
                .id(1L)
                .username("username")
                .build();
    }

    @Test
    void testAllByIds() {
        List<Long> ids = List.of(1L, 2L);
        when(userRedisRepository.findAllById(ids)).thenReturn(userRedisIterable);

        List<UserRedis> actual = userRedisService.getAllByIds(ids);

        verify(userRedisRepository, times(1)).findAllById(ids);
        assertEquals(userRedisIterable, actual);
    }

    @Test
    void testSave() {
        UserRedis userRedis = new UserRedis(userDto.getId(), userDto.getUsername());
        when(userRedisRepository.existsById(userDto.getId())).thenReturn(false);

        userRedisService.save(userDto);

        verify(userRedisRepository, times(1)).existsById(userDto.getId());
        verify(userRedisRepository, times(1)).save(userRedis);
    }

    @Test
    void testSaveWhenAlreadyExists() {
        when(userRedisRepository.existsById(userDto.getId())).thenReturn(true);

        userRedisService.save(userDto);

        verify(userRedisRepository, times(1)).existsById(userDto.getId());
        verify(userRedisRepository, times(0)).save(any(UserRedis.class));
    }

    @Test
    void testSaveAll() {
        userRedisService.saveAll(userRedisIterable);

        verify(userRedisRepository, times(1)).saveAll(userRedisIterable);
    }

    @Test
    void testExistsByIdTrue() {
        when(userRedisRepository.existsById(id)).thenReturn(true);

        assertTrue(userRedisService.existsById(id));
        verify(userRedisRepository, times(1)).existsById(id);
    }

    @Test
    void testExistsByIdFalse() {
        when(userRedisRepository.existsById(id)).thenReturn(false);

        assertFalse(userRedisService.existsById(id));
        verify(userRedisRepository, times(1)).existsById(id);
    }
}