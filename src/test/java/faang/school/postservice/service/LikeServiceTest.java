package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.utils.PostServiceUtils;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostServiceUtils postServiceUtils;
    @Mock
    private CommentService commentService;
    @Mock
    private UserServiceClient userServiceClient;
    @Spy
    private LikeMapper likeMapper;
    @InjectMocks
    private LikeService likeService;

    private Like likeEntity;

    @BeforeEach
    void setUp() {
        likeEntity = Like.builder()
                .userId(9999L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testAddLikeToPostAuthorNotFound() {
        when(userServiceClient.getUser(anyLong())).thenThrow(FeignException.class);
        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> likeService.addLikeToPost(1L, 1L));
        assertEquals("Like author not found", dataValidationException.getMessage());
    }

    @Test
    void testAddLikeToPostAuthorAlreadyLiked() {
        when(likeRepository.findByPostIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(Mockito.mock(Like.class)));
        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> likeService.addLikeToPost(1L, 1L));
        assertEquals("Post is already liked by this user", dataValidationException.getMessage());
    }

    @Test
    void testAddLikeToPostReturnedValue() {
        UserDto userDto = new UserDto(2343L, "test", "test");
        when(userServiceClient.getUser(anyLong())).thenReturn(userDto);
        when(likeRepository.findByPostIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(postServiceUtils.getPost(anyLong())).thenReturn(Mockito.mock(Post.class));
        when(likeRepository.save(any())).thenReturn(likeEntity);

        assertNotNull(likeRepository.save(any()));
        assertEquals(likeMapper.toDto(likeEntity), likeService.addLikeToPost(1L, 1L));
    }

    @Test
    void testRemoveInexistentAuthorsLikeFromPost() {
        when(likeRepository.findByPostIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        assertFalse(likeService.removeLikeFromPost(1L, 1L));
    }

    @Test
    void testRemoveLikeFromPostSuccesfull() {
        when(likeRepository.findByPostIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(new Like()));
        assertTrue(likeService.removeLikeFromPost(1L, 1L));
        verify(likeRepository).deleteByPostIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void testAddLikeToCommentAuthorNotFound() {
        when(userServiceClient.getUser(anyLong())).thenThrow(FeignException.class);
        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> likeService.addLikeToComment(1L, 1L));
        assertEquals("Like author not found", dataValidationException.getMessage());
    }

    @Test
    void testAddLikeToCommentAuthorAlreadyLiked() {
        when(likeRepository.findByCommentIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(Mockito.mock(Like.class)));
        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> likeService.addLikeToComment(1L, 1L));
        assertEquals("Comment is already liked by this user", dataValidationException.getMessage());
    }

    @Test
    void testAddLikeToCommentReturnedValue() {
        UserDto userDto = new UserDto(8364L, "test2", "test2");
        when(userServiceClient.getUser(anyLong())).thenReturn(userDto);
        when(likeRepository.findByCommentIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(commentService.getComment(anyLong())).thenReturn(Mockito.mock(Comment.class));
        when(likeRepository.save(any())).thenReturn(likeEntity);

        assertNotNull(likeRepository.save(any()));
        assertEquals(likeMapper.toDto(likeEntity), likeService.addLikeToComment(1L, 1L));
    }

    @Test
    void testRemoveInexistentAuthorsLikeFromComment() {
        when(likeRepository.findByCommentIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        assertFalse(likeService.removeLikeFromComment(1L, 1L));
    }

    @Test
    void testRemoveLikeFromCommentSuccesfull() {
        when(likeRepository.findByCommentIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(new Like()));
        assertTrue(likeService.removeLikeFromComment(1L, 1L));
        verify(likeRepository).deleteByCommentIdAndUserId(anyLong(), anyLong());
    }
}