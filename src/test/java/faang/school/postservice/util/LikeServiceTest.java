package faang.school.postservice.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.like.LikeService;
import faang.school.postservice.validator.LikeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private LikeValidator likeValidator;

    @Mock
    private LikeMapper likeMapper;

    @InjectMocks
    private LikeService likeService;

    private Long postId;
    private Long commentId;
    private Long userId;
    private LikeDto likeDto;
    private Like likeEntity;

    @BeforeEach
    void setUp() {
        postId = 1L;
        commentId = 1L;
        userId = 2L;

        likeDto = new LikeDto();
        likeDto.setUserId(userId);
        likeDto.setPostId(postId);
        likeDto.setCommentId(commentId);

        likeEntity = new Like();
        likeEntity.setUserId(userId);
        likeEntity.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void addLikeToPostTest_Success() {
        doNothing().when(likeValidator).userValidation(userId);
        doNothing().when(likeValidator).validatePostExists(postId);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());
        when(likeMapper.toEntity(likeDto)).thenReturn(likeEntity);
        when(likeRepository.save(likeEntity)).thenReturn(likeEntity);

        likeService.addLikeToPost(postId, likeDto);

        verify(likeRepository, times(1)).save(likeEntity);
        assertNotNull(likeEntity.getCreatedAt());
    }

    @Test
    void addLikeToPostTest_LikeAlreadyExists() {
        doNothing().when(likeValidator).userValidation(userId);
        doNothing().when(likeValidator).validatePostExists(postId);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.of(likeEntity));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            likeService.addLikeToPost(postId, likeDto);
        });
        assertEquals("Лайк уже поставлен", exception.getMessage());
        verify(likeRepository, never()).save(any());
    }

    @Test
    void removeLikeFromPostTest_Success() {
        doNothing().when(likeValidator).userValidation(userId);
        doNothing().when(likeValidator).validatePostExists(postId);
        likeService.removeLikeFromPost(postId, likeDto);

        verify(likeRepository, times(1)).deleteByPostIdAndUserId(postId, userId);
    }

    @Test
    void addLikeToCommentTest_Success() {
        doNothing().when(likeValidator).userValidation(userId);
        doNothing().when(likeValidator).validateCommentExists(commentId);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.empty());

        likeDto.setCommentId(commentId);
        when(likeMapper.toEntity(likeDto)).thenReturn(likeEntity);
        when(likeRepository.save(likeEntity)).thenReturn(likeEntity);

        likeService.addLikeToComment(commentId, likeDto);

        verify(likeRepository, times(1)).save(likeEntity);
        assertNotNull(likeEntity.getCreatedAt());
    }

    @Test
    void addLikeToCommentTest_LikeAlreadyExists() {
        doNothing().when(likeValidator).userValidation(userId);
        doNothing().when(likeValidator).validateCommentExists(commentId);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.of(likeEntity));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            likeService.addLikeToComment(commentId, likeDto);
        });

        assertEquals("Лайк уже поставлен", exception.getMessage());
        verify(likeRepository, never()).save(any());
    }

    @Test
    void removeLikeFromCommentTest_Success() {
        doNothing().when(likeValidator).userValidation(userId);
        doNothing().when(likeValidator).validateCommentExists(commentId);

        likeService.removeLikeFromComment(commentId, likeDto);

        verify(likeRepository, times(1)).deleteByCommentIdAndUserId(commentId, userId);
    }

    @Test
    void getLikesFromPostTest_Success() {
        List<Like> likes = Arrays.asList(new Like() {
            {
                setUserId(1L);
            }
        }, new Like() {
            {
                setUserId(2L);
            }
        }, new Like() {
            {
                setUserId(3L);
            }
        });

        when(likeRepository.findByPostId(postId)).thenReturn(likes);

        List<Long> result = likeService.getLikesFromPost(postId);

        assertEquals(3, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
        assertTrue(result.contains(3L));
    }

    @Test
    void getLikesFromCommentTest_Success() {
        List<Like> likes = Arrays.asList(new Like() {
            {
                setUserId(1L);
            }
        }, new Like() {
            {
                setUserId(2L);
            }
        }, new Like() {
            {
                setUserId(3L);
            }
        });

        when(likeRepository.findByCommentId(commentId)).thenReturn(likes);

        List<Long> result = likeService.getLikesFromComment(commentId);

        assertEquals(3, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
        assertTrue(result.contains(3L));
    }
}