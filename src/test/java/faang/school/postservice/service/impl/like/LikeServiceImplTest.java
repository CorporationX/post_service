package faang.school.postservice.service.impl.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.dto.like.LikeDto;
import faang.school.postservice.model.event.LikeEvent;
import faang.school.postservice.model.dto.user.UserDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.entity.Comment;
import faang.school.postservice.model.entity.Like;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.like.LikeValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private LikeMapper likeMapper;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private LikeValidator likeValidator;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private UserContext userContext;
    @Mock
    private LikeEventPublisher likeEventPublisher;
    @InjectMocks
    private LikeServiceImpl likeService;

    private long commentId;
    private long userId;
    private Comment comment;
    private Post post;
    private LikeDto likeDto;
    private Like like;
    private UserDto userDto;
    private long postId;

    @BeforeEach
    void setUp() {
        commentId = 1L;
        userId = 1L;
        postId = 1L;
        comment = new Comment();
        post = new Post();
        post.setAuthorId(1L);
        likeDto = new LikeDto();
        like = new Like();
        userDto = new UserDto(1L, "david", "david228", "david228@mail.ru");
    }

    @Test
    void createLikeComment() {
        likeDto.setCommentId(1L);
        likeDto.setUserId(1L);
        likeDto.setId(1L);
        likeDto.setPostId(null);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);

        when(likeValidator.validate(commentId, userId, commentRepository))
                .thenReturn(comment);
        when(likeRepository.save(any(Like.class))).thenReturn(like);
        when(likeMapper.toLikeDto(any(Like.class))).thenReturn(likeDto);

        var result = likeService.createLikeComment(commentId);

        verify(userContext).getUserId();
        verify(userServiceClient).getUser(userId);
        verify(likeValidator).validate(commentId, userId, commentRepository);
        verify(likeRepository).save(any(Like.class));
        verify(likeMapper).toLikeDto(any(Like.class));

        Assertions.assertEquals(likeDto, result);
    }

    @Test
    void deleteLikeComment() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);

        doNothing().when(likeRepository)
                .deleteByCommentIdAndUserId(anyLong(), anyLong());
        when(likeValidator.validateCommentOrPost(commentId, commentRepository))
                .thenReturn(comment);

        likeService.deleteLikeComment(commentId);

        verify(likeValidator).validateCommentOrPost(commentId, commentRepository);
        verify(likeRepository).deleteByCommentIdAndUserId(commentId, userId);
    }

    @Test
    void createLikePost() {
        likeDto.setCommentId(null);
        likeDto.setUserId(1L);
        likeDto.setId(1L);
        likeDto.setPostId(1L);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);

        when(likeValidator.validate(postId, userId, postRepository))
                .thenReturn(post);
        when(likeRepository.save(any(Like.class))).thenReturn(like);
        when(likeMapper.toLikeDto(any(Like.class))).thenReturn(likeDto);

        var result = likeService.createLikePost(postId);

        verify(userContext).getUserId();
        verify(userServiceClient).getUser(userId);
        verify(likeValidator).validate(postId, userId, postRepository);
        verify(likeRepository).save(any(Like.class));
        verify(likeMapper).toLikeDto(any(Like.class));
        verify(likeEventPublisher).publish(any(LikeEvent.class));

        Assertions.assertEquals(likeDto, result);
    }

    @Test
    void deleteLikePost() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);

        doNothing().when(likeRepository)
                .deleteByPostIdAndUserId(anyLong(), anyLong());
        when(likeValidator.validateCommentOrPost(postId, postRepository))
                .thenReturn(post);

        likeService.deleteLikePost(postId);

        verify(likeValidator).validateCommentOrPost(postId, postRepository);
        verify(likeRepository).deleteByPostIdAndUserId(postId, userId);
    }
}