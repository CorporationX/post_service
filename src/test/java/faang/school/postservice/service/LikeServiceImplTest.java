package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.CommentMapperImpl;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class LikeServiceImplTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Spy
    private PostMapperImpl postMapper;
    @Spy
    private LikeMapperImpl likeMapper;
    @Spy
    private CommentMapperImpl commentMapper;
    @Mock
    private PostService postService;
    @Mock
    private CommentService commentService;
    @Mock
    private UserContext userContext;
    @Captor
    private ArgumentCaptor<Like> likeCaptor;
    @Captor
    private ArgumentCaptor<Long> postIdCaptor;
    @Captor
    private ArgumentCaptor<Long> commentIdCaptor;
    @Captor
    private ArgumentCaptor<Long> userIdCaptor;

    @InjectMocks
    private LikeServiceImpl likeService;

    long userId;
    long postId;
    long commentId;

    @BeforeEach
    public void  init() {
         userId = 1L;
         postId = 2L;
         commentId = 3L;
    }

    @Test
    public void testAddLikeToPostWhenPostIsLiked() {
        LikeDto likeDto = new LikeDto(userId, postId, null);
        Like like1 = Like.builder().build();
        Post post = Post.builder().id(postId).build();
        Optional<Like> optionalLike = Optional.of(like1);
        UserDto userDto = new UserDto(userId,"ggf", "fef");
        when(postService.findPostById(likeDto.postId())).thenReturn(post);
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(likeRepository.findByPostIdAndUserId(postId,userId)).thenReturn(optionalLike);

        PostDto postDto = likeService.addLikeToPost(postId);
        verify(userContext,times(1)).getUserId();
        verify(postService,times(1)).findPostById(likeDto.postId());
        verify(userServiceClient, times(1)).getUser(userId);
        verify(likeRepository, times(1))
                .findByPostIdAndUserId(likeDto.postId(),likeDto.userId());
        verify(likeRepository, never()).save(eq(likeMapper.toEntity(likeDto)));
        assertNotNull(postDto);
    }

    @Test
    public void testAddLikeToPost() {
        LikeDto likeDto = new LikeDto(userId, postId, null);
        Post post = Post.builder().id(postId).likes(List.of(new Like(), new Like())).build();
        Optional<Like> optionalLike = Optional.empty();
        UserDto userDto = new UserDto(userId,"ggf", "fef");
        when(postService.findPostById(likeDto.postId())).thenReturn(post);
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(likeRepository.findByPostIdAndUserId(postId,userId)).thenReturn(optionalLike);

        PostDto postDto = likeService.addLikeToPost(postId);
        verify(userContext,times(1)).getUserId();
        verify(postService,times(1)).findPostById(likeDto.postId());
        verify(userServiceClient, times(1)).getUser(userId);
        verify(likeRepository, times(1))
                .findByPostIdAndUserId(likeDto.postId(),likeDto.userId());
        verify(likeRepository, times(1)).save(likeCaptor.capture());
        Like capturedLike = likeCaptor.getValue();
        assertNotNull(capturedLike);
        assertEquals(userId, capturedLike.getUserId());
        assertEquals(postId, capturedLike.getPost().getId());
        assertNotNull(postDto);
    }

    @Test
    public void testRemoveLikeFromPostWhenPostNotLiked() {
        LikeDto likeDto = new LikeDto(userId, postId, null);
        Post post = Post.builder().id(postId).build();
        Optional<Like> optionalLike = Optional.empty();
        UserDto userDto = new UserDto(userId,"ggf", "fef");
        when(postService.findPostById(likeDto.postId())).thenReturn(post);
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(likeRepository.findByPostIdAndUserId(postId,userId)).thenReturn(optionalLike);

        PostDto postDto = likeService.removeLikeFromPost(postId);
        verify(userContext,times(1)).getUserId();
        verify(postService,times(1)).findPostById(likeDto.postId());
        verify(userServiceClient, times(1)).getUser(userId);
        verify(likeRepository, times(1))
                .findByPostIdAndUserId(likeDto.postId(),likeDto.userId());
        verify(likeRepository, never()).deleteByPostIdAndUserId(likeDto.postId(),likeDto.userId());
        assertNotNull(postDto);
    }

    @Test
    public void testRemoveLikeFromPost() {
        LikeDto likeDto = new LikeDto(userId, postId, null);
        Post post = Post.builder().id(postId).likes(List.of(new Like(), new Like())).build();
        Like like1 = Like.builder().build();
        Optional<Like> optionalLike = Optional.of(like1);
        UserDto userDto = new UserDto(userId,"ggf", "fef");
        when(postService.findPostById(likeDto.postId())).thenReturn(post);
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(likeRepository.findByPostIdAndUserId(postId,userId)).thenReturn(optionalLike);

        PostDto postDto = likeService.removeLikeFromPost(postId);
        verify(userContext,times(1)).getUserId();
        verify(likeRepository, times(1))
                .deleteByPostIdAndUserId(postIdCaptor.capture(),userIdCaptor.capture());
        assertEquals(userId, userIdCaptor.getValue());
        assertEquals(postId, postIdCaptor.getValue());
        assertNotNull(postDto);
    }

    @Test
    public void testAddLikeToCommentWhenCommentIsLiked() {
        LikeDto likeDto = new LikeDto(userId, null, commentId);
        Like like1 = Like.builder().build();
        Comment comment = Comment.builder().id(commentId).build();
        Optional<Like> optionalLike = Optional.of(like1);
        UserDto userDto = new UserDto(userId,"ggf", "fef");
        when(commentService.findCommentById(likeDto.commentId())).thenReturn(comment);
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(likeRepository.findByCommentIdAndUserId(commentId,userId)).thenReturn(optionalLike);

        CommentDto commentDto = likeService.addLikeToComment(commentId);
        verify(userContext,times(1)).getUserId();
        verify(commentService,times(1)).findCommentById(likeDto.commentId());
        verify(userServiceClient, times(1)).getUser(userId);
        verify(likeRepository, times(1))
                .findByCommentIdAndUserId(likeDto.commentId(),likeDto.userId());
        verify(likeRepository, never()).save(eq(likeMapper.toEntity(likeDto)));
        assertNotNull(commentDto);
    }

    @Test
    public void testAddLikeToComment() {
        LikeDto likeDto = new LikeDto(userId, null, commentId);
        Comment comment = Comment.builder().id(commentId).build();
        Optional<Like> optionalLike = Optional.empty();
        UserDto userDto = new UserDto(userId,"ggf", "fef");
        when(commentService.findCommentById(likeDto.commentId())).thenReturn(comment);
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(likeRepository.findByCommentIdAndUserId(commentId,userId)).thenReturn(optionalLike);

        CommentDto commentDto = likeService.addLikeToComment(commentId);
        verify(userContext,times(1)).getUserId();
        verify(commentService,times(1)).findCommentById(likeDto.commentId());
        verify(userServiceClient, times(1)).getUser(userId);
        verify(likeRepository, times(1))
                .findByCommentIdAndUserId(likeDto.commentId(),likeDto.userId());
        verify(likeRepository, times(1)).save(likeCaptor.capture());
        Like caturedLike = likeCaptor.getValue();
        assertEquals(userId, caturedLike.getUserId());
        assertEquals(commentId, caturedLike.getComment().getId());
        assertNotNull(commentDto);
    }

    @Test
    public void testRemoveLikeFromCommentWhenCommentNotLiked() {
        LikeDto likeDto = new LikeDto(userId, null, commentId);
        Comment comment = Comment.builder().id(commentId).build();
        Optional<Like> optionalLike = Optional.empty();
        UserDto userDto = new UserDto(userId,"ggf", "fef");
        when(commentService.findCommentById(likeDto.commentId())).thenReturn(comment);
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(likeRepository.findByCommentIdAndUserId(commentId,userId)).thenReturn(optionalLike);

        CommentDto commentDto = likeService.removeLikeFromComment(commentId);
        verify(userContext,times(1)).getUserId();
        verify(commentService,times(1)).findCommentById(likeDto.commentId());
        verify(userServiceClient, times(1)).getUser(userId);
        verify(likeRepository, times(1))
                .findByCommentIdAndUserId(likeDto.commentId(),likeDto.userId());
        verify(likeRepository, never())
                .deleteByCommentIdAndUserId(likeDto.commentId(), likeDto.userId());
        assertNotNull(commentDto);
    }

    @Test
    public void testRemoveLikeFromComment() {
        LikeDto likeDto = new LikeDto(userId, null, commentId);
        Comment comment = Comment.builder().id(commentId).build();
        Like like1 = Like.builder().build();
        Optional<Like> optionalLike = Optional.of(like1);
        UserDto userDto = new UserDto(userId,"ggf", "fef");
        when(commentService.findCommentById(likeDto.commentId())).thenReturn(comment);
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(likeRepository.findByCommentIdAndUserId(commentId,userId)).thenReturn(optionalLike);

        CommentDto commentDto = likeService.removeLikeFromComment(commentId);
        verify(userContext,times(1)).getUserId();
        verify(commentService,times(1)).findCommentById(likeDto.commentId());
        verify(userServiceClient, times(1)).getUser(userId);
        verify(likeRepository, times(1))
                .findByCommentIdAndUserId(likeDto.commentId(),likeDto.userId());
        verify(likeRepository, times(1))
                .deleteByCommentIdAndUserId(commentIdCaptor.capture(), userIdCaptor.capture());
        assertEquals(userId, userIdCaptor.getValue());
        assertEquals(commentId, commentIdCaptor.getValue());
        assertNotNull(commentDto);
    }

}