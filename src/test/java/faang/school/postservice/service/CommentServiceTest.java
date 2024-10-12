package faang.school.postservice.service;

import faang.school.postservice.cache.entity.UserCache;
import faang.school.postservice.cache.repository.UserCacheRepository;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.event.CommentAchievementEvent;
import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.CommentAchievementMapper;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.redisPublisher.CommentAchievementEventPublisher;
import faang.school.postservice.redisPublisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private CommentValidator commentValidator;
    @Mock
    private UserContext userContext;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentEventPublisher commentEventPublisher;
    @Mock
    private CommentAchievementEventPublisher commentAchievementEventPublisher;
    @Mock
    private CommentAchievementMapper commentAchievementMapper;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private UserCacheRepository userCacheRepository;

    private long commentId;
    private long postId;
    private long userId;
    private Comment comment;
    private CommentDto commentDto;
    private CommentDto updatedCommentDto;
    private Post post;
    private CommentAchievementEvent commentAchievementEvent;
    private UserDto userDto;

    @BeforeEach
    void init() {
        commentId = 1L;
        userId = 2L;
        postId = 4L;
        String content = "content";
        userDto = UserDto.builder().build();
        post = Post.builder()
                .id(postId)
                .authorId(2L).build();
        comment = Comment.builder()
                .id(commentId)
                .authorId(userId)
                .content(content)
                .post(post)
                .build();
        commentDto = CommentDto.builder()
                .id(commentId)
                .authorId(userId)
                .postId(postId)
                .build();
        updatedCommentDto = CommentDto.builder()
                .id(commentId)
                .authorId(2L)
                .content("UpdatedContent")
                .build();

        commentAchievementEvent = CommentAchievementEvent.builder()
                .id(commentId)
                .postId(postId)
                .content(content)
                .build();
    }

    @Test
    void deleteCommentTest() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        commentService.deleteComment(commentId);
        verify(commentRepository).findById(commentId);
    }

    @Test
    void findAllByPostIdTest() {
        when(commentRepository.findAllByPostId(postId)).thenReturn(List.of(comment));
        when(commentMapper.entityToDto(comment)).thenReturn(commentDto);
        commentService.findAllByPostId(postId);
        verify(commentRepository).findAllByPostId(postId);
    }

    @Test
    void createCommentTest() {
        when(userContext.getUserId()).thenReturn(userId);
        when(commentMapper.dtoToEntity(commentDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(postRepository.findById(commentDto.getPostId())).thenReturn(Optional.of(post));
        when(commentMapper.entityToDto(comment)).thenReturn(commentDto);
        when(commentAchievementMapper.commentDtoToCommentAchievementEvent(commentDto)).thenReturn(commentAchievementEvent);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        CommentDto result = commentService.createComment(commentDto);
        CommentEvent commentEvent = CommentEvent.builder()
                .commentAuthorId(comment.getAuthorId())
                .postAuthorId(post.getAuthorId())
                .commentId(comment.getId())
                .build();
        verify(commentRepository).save(comment);
        verify(commentAchievementEventPublisher).publish(commentAchievementEvent);
        verify(userServiceClient, times(1)).getUser(userId);
        verify(userCacheRepository, times(1)).save(any(UserCache.class));
        assertNotNull(result);
        assertEquals(commentDto, result);
    }

    @Test
    void updateCommentTest() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.entityToDto(comment)).thenReturn(updatedCommentDto);
        CommentDto result = commentService.updateComment(updatedCommentDto);
        verify(commentRepository).findById(commentId);
        assertNotNull(updatedCommentDto);
        assertEquals(result.getContent(), updatedCommentDto.getContent());
    }

    @Test
    public void testGetCommentNotCommentDataBase() {
        long commentId = 1;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> commentService.getComment(commentId));
    }

    @Test
    public void testGetCommentWhenValid() {
        long commentId = 1;
        Comment comment = new Comment();
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        Comment result = commentService.getComment(commentId);
        assertDoesNotThrow(() -> commentService.getComment(commentId));
        assertEquals(comment, result);
    }
}
