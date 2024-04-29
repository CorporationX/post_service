package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaLikeProducer;
import faang.school.postservice.repository.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {
    @Mock
    private PostServiceImpl postServiceImpl;

    @Mock
    private CommentService commentService;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserContext userContext;
    @Mock
    private KafkaLikeProducer kafkaLikeProducer;
    @Spy
    private LikeMapperImpl likeMapper;


    @InjectMocks
    private LikeServiceImpl likeService;

    private UserDto userDto;

    private LikeDto likeDto;

    private Like like;

    private Post post;

    private Comment comment;

    private long userId = 1L;


    @BeforeEach
    public void setUp() {
        userDto = UserDto.builder()
                .id(userId)
                .build();

        likeDto = LikeDto.builder()
                .id(1L)
                .postId(1L)
                .commentId(1l)
                .build();

        post = Post.builder()
                .id(1L)
                .build();

        comment = Comment.builder()
                .id(1L)
                .build();

        like = Like.builder()
                .id(1L)
                .post(post)
                .comment(comment)
                .build();

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);

    }


    @Test
    public void testShouldLikePost() {
        when(postServiceImpl.searchPostById(likeDto.getPostId())).thenReturn(post);
        when(likeRepository.findByPostIdAndUserId(post.getId(), userDto.getId())).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(like);
        assertEquals(likeDto, likeService.likePost(likeDto));
    }


    @Test
    public void testShouldLikeComment() {
        when(commentService.getCommentIfExist(likeDto.getCommentId())).thenReturn(comment);
        when(likeRepository.findByCommentIdAndUserId(comment.getId(), userDto.getId())).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(like);
        assertEquals(likeDto, likeService.likeComment(likeDto));
    }


    @Test
    public void testShouldThrowDataValidationExceptionOnDuplicateLikePost() {
        when(postServiceImpl.searchPostById(likeDto.getPostId())).thenReturn(post);
        when(likeRepository.findByPostIdAndUserId(post.getId(), userDto.getId())).thenReturn(Optional.of(like));
        assertThrows(DataValidationException.class, () -> likeService.likePost(likeDto));
    }


    @Test
    public void testShouldThrowDataValidationExceptionOnDuplicateLikeComment() {
        when(commentService.getCommentIfExist(likeDto.getCommentId())).thenReturn(comment);
        when(likeRepository.findByCommentIdAndUserId(comment.getId(), userDto.getId())).thenReturn(Optional.of(like));
        assertThrows(DataValidationException.class, () -> likeService.likeComment(likeDto));
    }


    @Test
    public void testShouldDeleteLikePost() {
        likeService.deleteLikePost(post.getId());
        verify(likeRepository).deleteByPostIdAndUserId(post.getId(), userDto.getId());
    }


    @Test
    public void testShouldDeleteLikeComment() {
        likeService.deleteLikeComment(comment.getId());
        verify(likeRepository).deleteByCommentIdAndUserId(comment.getId(), userDto.getId());
    }

}
