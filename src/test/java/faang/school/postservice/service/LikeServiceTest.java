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
class LikeServiceTest {
    @Mock
    private PostService postService;

    @Mock
    private CommentService commentService;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserContext userContext;

    @Spy
    private LikeMapperImpl likeMapper;


    @InjectMocks
    private LikeServiceImpl likeService;

    private UserDto userDto;

    private LikeDto likeDto;

    private Like like;

    private Post post;

    private Comment comment;


    @BeforeEach
    void setUp() {
        long userId = 1L;
        userDto = UserDto.builder()
                .id(userId)
                .build();

        likeDto = LikeDto.builder()
                .id(1L)
                .postId(1L)
                .commentId(1L)
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
    void testShouldLikeComment() {
        when(commentService.getCommentIfExist(likeDto.getCommentId())).thenReturn(comment);
        when(likeRepository.findByCommentIdAndUserId(comment.getId(), userDto.getId())).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(like);
        assertEquals(likeDto, likeService.likeComment(likeDto));
    }


    @Test
    void testShouldThrowDataValidationExceptionOnDuplicateLikePost() {
        when(postService.searchPostById(likeDto.getPostId())).thenReturn(post);
        when(likeRepository.findByPostIdAndUserId(post.getId(), userDto.getId())).thenReturn(Optional.of(like));
        assertThrows(DataValidationException.class, () -> likeService.likePost(likeDto));
    }


    @Test
    void testShouldThrowDataValidationExceptionOnDuplicateLikeComment() {
        when(commentService.getCommentIfExist(likeDto.getCommentId())).thenReturn(comment);
        when(likeRepository.findByCommentIdAndUserId(comment.getId(), userDto.getId())).thenReturn(Optional.of(like));
        assertThrows(DataValidationException.class, () -> likeService.likeComment(likeDto));
    }


    @Test
    void testShouldDeleteLikePost() {
        likeService.deleteLikePost(post.getId());
        verify(likeRepository).deleteByPostIdAndUserId(post.getId(), userDto.getId());
    }


    @Test
    void testShouldDeleteLikeComment() {
        likeService.deleteLikeComment(comment.getId());
        verify(likeRepository).deleteByCommentIdAndUserId(comment.getId(), userDto.getId());
    }

}
