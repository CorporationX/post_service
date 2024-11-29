package faang.school.postservice.service.cache;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.repository.cache.ListCacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeCacheServiceTest {

    @Mock
    private ListCacheRepository<LikeDto> listCacheRepository;

    @InjectMocks
    private LikeCacheService likeCacheService;

    private Long postId;
    private String listKey;
    private LikeDto likeDto2;
    private LikeDto likeDto1;
    private List<LikeDto> likes;

    @BeforeEach
    void setUp() {
        postId = 1L;
        listKey = postId + "::post_likes";
        likeDto2 = new LikeDto();
        likeDto1 = new LikeDto();
        likes = List.of(likeDto1, likeDto2);
    }

    @Test
    void save_ShouldCallRepositoryRightPushWithCorrectArguments() {
        likeCacheService.save(postId, likeDto1);

        verify(listCacheRepository).rightPush(listKey, likeDto1);
    }

    @Test
    void get_ShouldReturnFirstLike_WhenLikesExist() {
        when(listCacheRepository.get(listKey)).thenReturn(likes);

        LikeDto result = likeCacheService.get(postId);

        assertNotNull(result);
        assertEquals(likeDto1, result);
    }


    @Test
    void get_ShouldReturnNull_WhenNoLikesExist() {
        when(listCacheRepository.get(listKey)).thenReturn(Collections.emptyList());

        LikeDto result = likeCacheService.get(postId);

        assertNull(result);
    }

    @Test
    void getAll_ShouldReturnAllLikes_WhenLikesExist() {
        when(listCacheRepository.get(listKey)).thenReturn(likes);

        List<LikeDto> result = likeCacheService.getAll(postId);

        assertNotNull(result);
        assertEquals(likes, result);
    }

    @Test
    void getAll_ShouldReturnEmptyList_WhenNoLikesExist() {
        when(listCacheRepository.get(listKey)).thenReturn(Collections.emptyList());

        List<LikeDto> result = likeCacheService.getAll(postId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
