package faang.school.postservice.service.impl.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.publisher.EventPublisher;
import faang.school.postservice.validator.like.LikeValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private EventPublisher likeEventPublisher;
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

        Mockito.when(userContext.getUserId()).thenReturn(userId);
        Mockito.when(userServiceClient.getUser(userId)).thenReturn(userDto);

        Mockito.when(likeValidator.validate(commentId, userId, commentRepository))
                .thenReturn(comment);
        Mockito.when(likeRepository.save(Mockito.any(Like.class))).thenReturn(like);
        Mockito.when(likeMapper.toLikeDto(Mockito.any(Like.class))).thenReturn(likeDto);

        var result = likeService.createLikeComment(commentId);

        Mockito.verify(userContext).getUserId();
        Mockito.verify(userServiceClient).getUser(userId);
        Mockito.verify(likeValidator).validate(commentId, userId, commentRepository);
        Mockito.verify(likeRepository).save(Mockito.any(Like.class));
        Mockito.verify(likeMapper).toLikeDto(Mockito.any(Like.class));

        Assertions.assertEquals(likeDto, result);
    }

    @Test
    void deleteLikeComment() {
        Mockito.when(userContext.getUserId()).thenReturn(userId);
        Mockito.when(userServiceClient.getUser(userId)).thenReturn(userDto);

        Mockito.doNothing().when(likeRepository)
                .deleteByCommentIdAndUserId(Mockito.anyLong(), Mockito.anyLong());
        Mockito.when(likeValidator.validateCommentOrPost(commentId, commentRepository))
                .thenReturn(comment);

        likeService.deleteLikeComment(commentId);

        Mockito.verify(likeValidator).validateCommentOrPost(commentId, commentRepository);
        Mockito.verify(likeRepository).deleteByCommentIdAndUserId(commentId, userId);
    }

    @Test
    void createLikePost() {
        likeDto.setCommentId(null);
        likeDto.setUserId(1L);
        likeDto.setId(1L);
        likeDto.setPostId(1L);

        Mockito.when(userContext.getUserId()).thenReturn(userId);
        Mockito.when(userServiceClient.getUser(userId)).thenReturn(userDto);

        Mockito.when(likeValidator.validate(postId, userId, postRepository))
                .thenReturn(post);
        Mockito.when(likeRepository.save(Mockito.any(Like.class))).thenReturn(like);
        Mockito.when(likeMapper.toLikeDto(Mockito.any(Like.class))).thenReturn(likeDto);

        var result = likeService.createLikePost(postId);

        Mockito.verify(userContext).getUserId();
        Mockito.verify(userServiceClient).getUser(userId);
        Mockito.verify(likeValidator).validate(postId, userId, postRepository);
        Mockito.verify(likeRepository).save(Mockito.any(Like.class));
        Mockito.verify(likeMapper).toLikeDto(Mockito.any(Like.class));

        Assertions.assertEquals(likeDto, result);
    }

    @Test
    void deleteLikePost() {
        Mockito.when(userContext.getUserId()).thenReturn(userId);
        Mockito.when(userServiceClient.getUser(userId)).thenReturn(userDto);

        Mockito.doNothing().when(likeRepository)
                .deleteByPostIdAndUserId(Mockito.anyLong(), Mockito.anyLong());
        Mockito.when(likeValidator.validateCommentOrPost(postId, postRepository))
                .thenReturn(post);

        likeService.deleteLikePost(postId);

        Mockito.verify(likeValidator).validateCommentOrPost(postId, postRepository);
        Mockito.verify(likeRepository).deleteByPostIdAndUserId(postId, userId);
    }
}