package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.ValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LikeServiceTests {

    @InjectMocks
    private LikeService likeService;
    @Spy
    private LikeMapper likeMapper;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserServiceClient userServiceClient;

    private LikeDto likeDto;

    private Post post;
    private Comment comment;

    private final long USER_ID = 1L;
    private final long POST_ID = 3L;
    private final long COMMENT_ID = 4L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        likeDto = new LikeDto(USER_ID, POST_ID, COMMENT_ID);
        post = Post.builder()
                .id(POST_ID)
                .build();
        comment = Comment.builder()
                .id(COMMENT_ID)
                .build();
    }

    @Test
    public void testLikePostSuccess() {
        when(userServiceClient.getUser(USER_ID)).thenReturn(any());
        when(postRepository.findById(USER_ID)).thenReturn(Optional.of(post));
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.empty());
        Like like = Like.builder()
                .userId(USER_ID)
                .post(post)
                .build();
        when(likeRepository.save(like)).thenReturn(like);
        when(likeMapper.toLikeDto(any(Like.class))).thenReturn(likeDto);

        LikeDto result = likeService.likePost(USER_ID, likeDto);

        assertEquals(likeDto, result);
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    public void testLikePostAlreadyLiked() {
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.of(new Like()));

        Exception exception = assertThrows(ValidationException.class, () -> likeService.likePost(POST_ID, likeDto));
        assertEquals("Post with id 3 already has a like from user with id 1", exception.getMessage());
    }

    @Test
    public void testLikePostNonExistentPost() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> likeService.likePost(POST_ID, likeDto));
        assertEquals("Post with id 3 does not exist", exception.getMessage());
    }

    @Test
    public void testDeleteLikeFromPostSuccess() {
        likeService.deleteLikeFromPost(POST_ID, likeDto);

        verify(likeRepository).deleteByPostIdAndUserId(POST_ID, USER_ID);
    }

    @Test
    public void testLikeCommentSuccess() {
        when(userServiceClient.getUser(USER_ID)).thenReturn(any());
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.empty());
        Like like = Like.builder()
                .userId(USER_ID)
                .comment(comment)
                .build();
        when(likeRepository.save(like)).thenReturn(like);
        when(likeMapper.toLikeDto(any(Like.class))).thenReturn(likeDto);

        LikeDto result = likeService.likeComment(COMMENT_ID, likeDto);

        assertEquals(likeDto, result);
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    public void testLikeCommentAlreadyLiked() {
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.of(new Like()));

        Exception exception = assertThrows(ValidationException.class, () -> likeService.likeComment(COMMENT_ID, likeDto));
        assertEquals("Comment with id 4 already has a like from user with id 1", exception.getMessage());
    }

    @Test
    public void testLikeCommentNonExistentComment() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> likeService.likeComment(COMMENT_ID, likeDto));
        assertEquals("Comment with id 4 does not exist", exception.getMessage());
    }

    @Test
    public void testDeleteLikeFromCommentSuccess() {
        likeService.deleteLikeFromComment(COMMENT_ID, likeDto);

        verify(likeRepository).deleteByCommentIdAndUserId(COMMENT_ID, USER_ID);
    }
}
