package faang.school.postservice.cache.service;

import faang.school.postservice.cache.model.CacheableUser;
import faang.school.postservice.cache.repository.CacheableUserRepository;
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
class CacheableUserServiceTest {
    @InjectMocks
    private CacheableUserService cacheableUserService;
    @Mock
    private CacheableUserRepository cacheableUserRepository;

    private Iterable<CacheableUser> cacheableUserIterable;
    private UserDto userDto;
    private Long id;

    @BeforeEach
    void setUp() {
        id = 1L;
        cacheableUserIterable = List.of(
                new CacheableUser(1L, "username"),
                new CacheableUser(2L, "username")
        );
        userDto = UserDto.builder()
                .id(1L)
                .username("username")
                .build();
    }

    @Test
    void testAllByIds() {
        List<Long> ids = List.of(1L, 2L);
        when(cacheableUserRepository.findAllById(ids)).thenReturn(cacheableUserIterable);

        List<CacheableUser> actual = cacheableUserService.getAllByIds(ids);

        verify(cacheableUserRepository, times(1)).findAllById(ids);
        assertEquals(cacheableUserIterable, actual);
    }

    @Test
    void testSave() {
        CacheableUser cacheableUser = new CacheableUser(userDto.getId(), userDto.getUsername());
        when(cacheableUserRepository.existsById(userDto.getId())).thenReturn(false);

        cacheableUserService.save(userDto);

        verify(cacheableUserRepository, times(1)).existsById(userDto.getId());
        verify(cacheableUserRepository, times(1)).save(cacheableUser);
    }

    @Test
    void testSaveWhenAlreadyExists() {
        when(cacheableUserRepository.existsById(userDto.getId())).thenReturn(true);

        cacheableUserService.save(userDto);

        verify(cacheableUserRepository, times(1)).existsById(userDto.getId());
        verify(cacheableUserRepository, times(0)).save(any(CacheableUser.class));
    }

    @Test
    void testSaveAll() {
        cacheableUserService.saveAll(cacheableUserIterable);

        verify(cacheableUserRepository, times(1)).saveAll(cacheableUserIterable);
    }

    @Test
    void testExistsByIdTrue() {
        when(cacheableUserRepository.existsById(id)).thenReturn(true);

        assertTrue(cacheableUserService.existsById(id));
        verify(cacheableUserRepository, times(1)).existsById(id);
    }

    @Test
    void testExistsByIdFalse() {
        when(cacheableUserRepository.existsById(id)).thenReturn(false);

        assertFalse(cacheableUserService.existsById(id));
        verify(cacheableUserRepository, times(1)).existsById(id);
    }
}