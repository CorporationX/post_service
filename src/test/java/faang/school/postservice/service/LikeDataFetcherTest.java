package faang.school.postservice.service;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeDataFetcherTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private LikeMapper likeMapper;

    @InjectMocks
    private LikeDataFetcher likeService;

    private Pageable pageable;
    private Like like1;
    private Like like2;
    private LikeDto likeDto1;
    private LikeDto likeDto2;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);

        Post post = Post.builder().id(1L).build();
        Comment comment = Comment.builder().id(1L).build();

        like1 = Like.builder().id(1L).userId(100L).post(post).createdAt(LocalDateTime.now().minusHours(1)).build();
        like2 = Like.builder().id(2L).userId(101L).comment(comment).createdAt(LocalDateTime.now()).build();

        likeDto1 = LikeDto.builder()
                .id(1L)
                .userId(100L).
                createdAt(like1.getCreatedAt())
                .build();
        likeDto2 = LikeDto.builder()
                .id(2L).
                userId(101L).
                createdAt(like2.getCreatedAt())
                .build();
    }

    @Test
    void testGetLikesPageByPostId_Success() {
        Long postId = 1L;
        List<Like> likes = List.of(like1);
        Page<Like> likePage = new PageImpl<>(likes, pageable, 1);

        when(likeRepository.existsLikesByPostId(postId)).thenReturn(true);
        when(likeRepository.getLikesByPostId(postId, pageable)).thenReturn(likePage);
        when(likeMapper.toDto(like1)).thenReturn(likeDto1);

        Page<LikeDto> result = likeService.getLikesPageByPostId(postId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(likeDto1, result.getContent().get(0));
        verify(likeRepository).existsLikesByPostId(postId);
        verify(likeRepository).getLikesByPostId(postId, pageable);
        verify(likeMapper).toDto(like1);
    }

    @Test
    void testGetLikesPageByPostId_NullPostId_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> likeService.getLikesPageByPostId(null, pageable));
        assertEquals("postId cannot be null.", exception.getMessage());
        verify(likeRepository, never()).existsLikesByPostId(any());
        verify(likeRepository, never()).getLikesByPostId(any(), any());
    }

    @Test
    void testGetLikesPageByPostId_NoLikesExist_ReturnsEmptyPage() {
        Long postId = 2L;
        when(likeRepository.existsLikesByPostId(postId)).thenReturn(false);

        Page<LikeDto> result = likeService.getLikesPageByPostId(postId, pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(likeRepository).existsLikesByPostId(postId);
        verify(likeRepository, never()).getLikesByPostId(any(), any());
    }

    @Test
    void testGetLikesPageByPostId_RepositoryReturnsEmptyList_ReturnsEmptyPage() {
        Long postId = 1L;
        Page<Like> emptyLikePage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(likeRepository.existsLikesByPostId(postId)).thenReturn(true);
        when(likeRepository.getLikesByPostId(postId, pageable)).thenReturn(emptyLikePage);

        Page<LikeDto> result = likeService.getLikesPageByPostId(postId, pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(likeRepository).existsLikesByPostId(postId);
        verify(likeRepository).getLikesByPostId(postId, pageable);
        verify(likeMapper, never()).toDto(any());
    }

    @Test
    void testGetLikesPageByCommentId_Success() {
        Long commentId = 1L;
        List<Like> likes = List.of(like2);
        Page<Like> likePage = new PageImpl<>(likes, pageable, 1);

        when(likeRepository.existsLikesByCommentId(commentId)).thenReturn(true);
        when(likeRepository.getLikesByCommentId(commentId, pageable)).thenReturn(likePage);
        when(likeMapper.toDto(like2)).thenReturn(likeDto2);

        Page<LikeDto> result = likeService.getLikesPageByCommentId(commentId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(likeDto2, result.getContent().get(0));
        verify(likeRepository).existsLikesByCommentId(commentId);
        verify(likeRepository).getLikesByCommentId(commentId, pageable);
        verify(likeMapper).toDto(like2);
    }

    @Test
    void testGetLikesPageByCommentId_NullCommentId_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> likeService.getLikesPageByCommentId(null, pageable));
        assertEquals("commentId cannot be null.", exception.getMessage());
        verify(likeRepository, never()).existsLikesByCommentId(any());
        verify(likeRepository, never()).getLikesByCommentId(any(), any());
    }

    @Test
    void testGetLikesPageByCommentId_NoLikesExist_ReturnsEmptyPage() {
        Long commentId = 2L;
        when(likeRepository.existsLikesByCommentId(commentId)).thenReturn(false);

        Page<LikeDto> result = likeService.getLikesPageByCommentId(commentId, pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(likeRepository).existsLikesByCommentId(commentId);
        verify(likeRepository, never()).getLikesByCommentId(any(), any());
    }

    @Test
    void testGetLikesPageByCommentId_RepositoryReturnsEmptyList_ReturnsEmptyPage() {
        Long commentId = 1L;
        Page<Like> emptyLikePage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(likeRepository.existsLikesByCommentId(commentId)).thenReturn(true);
        when(likeRepository.getLikesByCommentId(commentId, pageable)).thenReturn(emptyLikePage);

        Page<LikeDto> result = likeService.getLikesPageByCommentId(commentId, pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(likeRepository).existsLikesByCommentId(commentId);
        verify(likeRepository).getLikesByCommentId(commentId, pageable);
        verify(likeMapper, never()).toDto(any());
    }


    // Tests for getLikesCountByPostId
    @Test
    void testGetLikesCountByPostId_Success() {
        Long postId = 1L;
        long expectedCount = 5L;
        Page<Like> likePage = new PageImpl<>(Collections.emptyList(), Pageable.unpaged(), expectedCount); // Content doesn't matter here

        when(likeRepository.getLikesByPostId(postId, Pageable.unpaged())).thenReturn(likePage);

        long result = likeService.getLikesCountByPostId(postId);

        assertEquals(expectedCount, result);
        verify(likeRepository).getLikesByPostId(postId, Pageable.unpaged());
    }

    @Test
    void testGetLikesCountByPostId_NullPostId_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> likeService.getLikesCountByPostId(null));
        assertEquals("postId cannot be null.", exception.getMessage());
        verify(likeRepository, never()).getLikesByPostId(any(), any());
    }

    @Test
    void testGetLikesCountByPostId_NoLikes_ReturnsZero() {
        Long postId = 1L;
        Page<Like> emptyLikePage = new PageImpl<>(Collections.emptyList(), Pageable.unpaged(), 0);

        when(likeRepository.getLikesByPostId(postId, Pageable.unpaged())).thenReturn(emptyLikePage);

        long result = likeService.getLikesCountByPostId(postId);

        assertEquals(0, result);
        verify(likeRepository).getLikesByPostId(postId, Pageable.unpaged());
    }

    @Test
    void testGetLikesCountByCommentId_Success() {
        Long commentId = 1L;
        long expectedCount = 3L;
        Page<Like> likePage = new PageImpl<>(Collections.emptyList(), Pageable.unpaged(), expectedCount); // Content doesn't matter

        when(likeRepository.getLikesByCommentId(commentId, Pageable.unpaged())).thenReturn(likePage);

        long result = likeService.getLikesCountByCommentId(commentId);

        assertEquals(expectedCount, result);
        verify(likeRepository).getLikesByCommentId(commentId, Pageable.unpaged());
    }

    @Test
    void testGetLikesCountByCommentId_NullCommentId_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> likeService.getLikesCountByCommentId(null));
        assertEquals("commentId cannot be null.", exception.getMessage());
        verify(likeRepository, never()).getLikesByCommentId(any(), any());
    }

    @Test
    void testGetLikesCountByCommentId_NoLikes_ReturnsZero() {
        Long commentId = 1L;
        Page<Like> emptyLikePage = new PageImpl<>(Collections.emptyList(), Pageable.unpaged(), 0);

        when(likeRepository.getLikesByCommentId(commentId, Pageable.unpaged()))
                .thenReturn(emptyLikePage);

        long result = likeService.getLikesCountByCommentId(commentId);

        assertEquals(0, result);
        verify(likeRepository).getLikesByCommentId(commentId, Pageable.unpaged());
    }
}