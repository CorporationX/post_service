package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.CommentLikeDto;
import faang.school.postservice.dto.like.PostLikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.ad.LikeRepository;
import faang.school.postservice.service.like.LikeService;
import faang.school.postservice.validator.LikeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostService postService;
    @Mock
    private CommentService commentService;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private LikeValidator likeValidator;

    private LikeMapper likeMapper;
    private LikeService likeService;
    private PostLikeDto postLikeDto;
    private CommentLikeDto commentLikeDto;
    private UserDto userDto;
    private Post post;
    private Comment comment;
    private Like like;

    @BeforeEach
    void setup() {
        likeMapper = Mappers.getMapper(LikeMapper.class);
        likeService = new LikeService(
                likeRepository,
                postService,
                commentService,
                likeMapper,
                userServiceClient,
                likeValidator
        );

        postLikeDto = new PostLikeDto(1L, 1L);
        commentLikeDto = new CommentLikeDto(1L, 1L);
        userDto = new UserDto(1L, "TestUser", "test@example.com");

        post = new Post();
        post.setId(1L);

        comment = new Comment();
        comment.setId(1L);

        like = new Like();
    }

    @Test
    void likePost_ShouldCreateLikeSuccessfully() {
        when(userServiceClient.getUser(postLikeDto.getUserId())).thenReturn(userDto);
        when(postService.getPost(postLikeDto.getPostId())).thenReturn(post);
        when(likeRepository.findByPostIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

        like = likeMapper.toLike(postLikeDto);
        like.setPost(post);

        likeService.likePost(postLikeDto);

        verify(userServiceClient).getUser(postLikeDto.getUserId());
        verify(likeValidator).validateUserExists(userDto);
        verify(postService).getPost(postLikeDto.getPostId());
        verify(likeRepository).save(like);
    }

    @Test
    void likePost_ShouldThrowExceptionWhenAlreadyLiked() {
        when(userServiceClient.getUser(postLikeDto.getUserId())).thenReturn(userDto);
        when(postService.getPost(postLikeDto.getPostId())).thenReturn(post);
        when(likeRepository.findByPostIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(new Like()));

        assertThrows(DataValidationException.class, () -> likeService.likePost(postLikeDto));
    }

    @Test
    void unlikePost_ShouldRemoveLikeSuccessfully() {
        when(userServiceClient.getUser(postLikeDto.getUserId())).thenReturn(userDto);
        when(postService.getPost(postLikeDto.getPostId())).thenReturn(post);

        likeService.unlikePost(postLikeDto);

        verify(likeRepository).deleteByPostIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void likeComment_ShouldCreateLikeSuccessfully() {
        when(userServiceClient.getUser(commentLikeDto.getUserId())).thenReturn(userDto);
        when(commentService.getComment(commentLikeDto.getCommentId())).thenReturn(comment);
        when(likeRepository.findByCommentIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

        like = likeMapper.toLike(commentLikeDto);
        like.setComment(comment);

        likeService.likeComment(commentLikeDto);

        verify(userServiceClient).getUser(commentLikeDto.getUserId());
        verify(likeValidator).validateUserExists(userDto);
        verify(commentService).getComment(commentLikeDto.getCommentId());
        verify(likeRepository).save(like);
    }

    @Test
    void likeComment_ShouldThrowExceptionWhenAlreadyLiked() {
        when(userServiceClient.getUser(commentLikeDto.getUserId())).thenReturn(userDto);
        when(commentService.getComment(commentLikeDto.getCommentId())).thenReturn(comment);
        when(likeRepository.findByCommentIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(new Like()));

        assertThrows(DataValidationException.class, () -> likeService.likeComment(commentLikeDto));
    }

    @Test
    void unlikeComment_ShouldRemoveLikeSuccessfully() {
        when(userServiceClient.getUser(commentLikeDto.getUserId())).thenReturn(userDto);
        when(commentService.getComment(commentLikeDto.getCommentId())).thenReturn(comment);

        likeService.unlikeComment(commentLikeDto);

        verify(likeRepository).deleteByCommentIdAndUserId(anyLong(), anyLong());
    }
}