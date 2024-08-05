package faang.school.postservice.like;


import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.validator.LikeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private LikeMapper likeMapper;

    @Mock
    private LikeValidator likeValidator;


    LikeDto likeDto;
    private Post post;
    private Comment comment;
    private Like like;

    @InjectMocks
    private LikeService likeService;

    @BeforeEach
    public void setUp() {
        likeDto = new LikeDto();
        likeDto.setUserId(1L);
        likeDto.setPostId(1L);
        likeDto.setCommentId(1L);

        post = new Post();
        post.setId(1L);

        comment = new Comment();
        comment.setId(1L);

        like = new Like();
        like.setId(1L);
        like.setUserId(1L);
        like.setPost(post);
        like.setComment(comment);
    }


    @Test
    void testLikePost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likeMapper.toEntity(likeDto)).thenReturn(like);
        when(likeRepository.findByPostIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        likeService.likePost(likeDto);

        verify(likeRepository).save(like);
    }

    @Test
    void testUnlikePost() {
        when(likeRepository.findByPostIdAndUserId(1L, 1L)).thenReturn(Optional.of(like));
        likeService.unlikePost(likeDto);
        verify(likeRepository).delete(like);
    }

    @Test
    void testUnlikePostNotFound() {
        when(likeRepository.findByPostIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> likeService.unlikePost(likeDto));
        assertEquals("Лайк не найден", exception.getMessage());
    }

    @Test
    void testLikeComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        when(likeMapper.toEntity(likeDto)).thenReturn(like);
        likeService.likeComment(likeDto);
        verify(likeRepository, times(1)).save(like);
    }

    @Test
    void testUnlikeComment() {
        when(likeRepository.findByCommentIdAndUserId(1L, 1L)).thenReturn(Optional.of(like));
        likeService.unlikeComment(likeDto);
        verify(likeRepository, times(1)).delete(like);
    }

    @Test
    void testUnlikeCommentNotFound() {
        when(likeRepository.findByCommentIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> likeService.unlikeComment(likeDto));
        assertEquals("Лайк не найден", exception.getMessage());
    }
}
