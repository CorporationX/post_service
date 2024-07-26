package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisCacheServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private RedisCacheService redisCacheService;

    private PostDto postDto;
    private UserDto userDto;
    private String json;
    private long id;

    @BeforeEach
    public void setUp() {
        postDto = PostDto.builder()
                .id(1L)
                .content("content1")
                .authorId(1L)
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .email("email")
                .username("name").build();
        json = "";
        id = 1L;
        ReflectionTestUtils.setField(redisCacheService, "ttlPost", 1L);
        ReflectionTestUtils.setField(redisCacheService, "timeUnitPost", "DAYS");
        ReflectionTestUtils.setField(redisCacheService, "ttlAuthor", 1L);
        ReflectionTestUtils.setField(redisCacheService, "timeUnitAuthor", "DAYS");
    }

    @Test
    public void testSavePost() throws JsonProcessingException {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(objectMapper.writeValueAsString(postDto)).thenReturn(json);
        String key = "post:" + postDto.getId();

        redisCacheService.savePost(postDto);

        verify(valueOperations).set(eq(key), eq(json), eq(1L), eq(TimeUnit.DAYS));
    }

    @Test
    public void testGetPost() throws JsonProcessingException {
        String key = "post:" + id;
        PostDto expectedPostDto = PostDto.builder()
                .id(1L)
                .content("content1")
                .authorId(1L)
                .build();
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(json);
        when(objectMapper.readValue(json, PostDto.class)).thenReturn(expectedPostDto);

        PostDto actualPostDto = redisCacheService.getPost(id);

        assertEquals(expectedPostDto, actualPostDto);
    }

    @Test
    public void testSaveAuthor() throws JsonProcessingException {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(objectMapper.writeValueAsString(userDto)).thenReturn(json);
        String key = "author:" + userDto.getId();

        redisCacheService.saveAuthor(userDto);

        verify(valueOperations).set(eq(key), eq(json), eq(1L), eq(TimeUnit.DAYS));
    }

    @Test
    public void testGetAuthor() throws JsonProcessingException {
        String key = "author:" + id;
        UserDto expectedUserDto = UserDto.builder()
                .id(1L)
                .email("email")
                .username("name").build();
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(json);
        when(objectMapper.readValue(json, UserDto.class)).thenReturn(expectedUserDto);

        UserDto actualUserDto = redisCacheService.getAuthor(id);

        assertEquals(expectedUserDto, actualUserDto);
    }
}
