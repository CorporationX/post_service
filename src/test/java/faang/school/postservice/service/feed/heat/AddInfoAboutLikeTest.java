package faang.school.postservice.service.feed.heat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.cache.RedisCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AddInfoAboutLikeTest {

    @InjectMocks
    private AddInfoAboutLike addInfoAboutLike;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private LikeMapper likeMapper;

    @Mock
    private RedisCacheService redisCacheService;

    @Mock
    private ObjectMapper objectMapper;

    private Like like;
    private LikeDto likeDto;

    @BeforeEach
    public void setUp() {
        like = Like.builder().build();
        likeDto = LikeDto.builder().id(1L).userId(1L).build();
    }

    @Test
    public void testAddInfoToRedis() throws JsonProcessingException {
        Long userId = 1L;
        Long postId = 1L;

        List<Like> likeList = List.of(like);
        List<LikeDto> likeDtoList = List.of(likeDto);
        String likeJson = "{\"id\":1,\"postId\":1,\"userId\":1}";

        when(likeRepository.findByPostId(postId)).thenReturn(likeList);
        when(likeMapper.toDto(any(Like.class))).thenReturn(likeDto);
        when(objectMapper.writeValueAsString(any(LikeDto.class))).thenReturn(likeJson);

        addInfoAboutLike.addInfoToRedis(userId, postId);

        verify(likeRepository, times(1)).findByPostId(postId);
        verify(likeMapper, times(1)).toDto(any(Like.class));
        verify(objectMapper, times(1)).writeValueAsString(any(LikeDto.class));
        verify(redisCacheService, times(1)).addLikeToCache(postId, likeJson);
    }

    @Test
    public void testAddInfoToRedisWithJsonProcessingException() throws JsonProcessingException {
        Long userId = 1L;
        Long postId = 1L;

        List<Like> likeList = List.of(like);
        List<LikeDto> likeDtoList = List.of(likeDto);

        when(likeRepository.findByPostId(postId)).thenReturn(likeList);
        when(likeMapper.toDto(any(Like.class))).thenReturn(likeDto);
        when(objectMapper.writeValueAsString(any(LikeDto.class))).thenThrow(new JsonProcessingException("Error") {});

        RuntimeException exception = assertThrows(RuntimeException.class, () -> addInfoAboutLike.addInfoToRedis(userId, postId));

        assertEquals("Error", exception.getCause().getMessage());

        verify(likeRepository, times(1)).findByPostId(postId);
        verify(likeMapper, times(1)).toDto(any(Like.class));
        verify(objectMapper, times(1)).writeValueAsString(any(LikeDto.class));
        verify(redisCacheService, times(0)).addLikeToCache(any(Long.class), any(String.class));
    }
}
