package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaCommentProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.RedisUserRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validator.comment.CommentValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    private static final long POST_ID = 1L;
    private static final Long AUTHOR_ID = 1L;
    private static final long COMMENT_ID = 1L;
    private static final String COMMENT_CONTENT = "Content";

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private CommentValidator commentValidator;
    @Mock
    private PostService postService;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private RedisUserRepository redisUserRepository;
    @Mock
    private KafkaCommentProducer kafkaCommentProducer;
    @Mock
    private UserContext userContext;

    @InjectMocks
    private CommentService commentService;

    private Post post;
    private Comment commentEntity;
    private Comment savedCommentEntity;
    private CommentDto commentDto;
    private CommentDto savedCommentDto;
    private UserDto userDto;

    @BeforeEach
    public void init() {
        post = Post.builder()
                .id(POST_ID)
                .build();
        commentEntity = Comment.builder()
                .authorId(AUTHOR_ID)
                .build();
        savedCommentEntity = Comment.builder()
                .id(COMMENT_ID)
                .authorId(AUTHOR_ID)
                .build();
        commentDto = CommentDto.builder()
                .authorId(AUTHOR_ID)
                .postId(POST_ID)
                .build();
        savedCommentDto = CommentDto.builder()
                .id(COMMENT_ID)
                .authorId(AUTHOR_ID)
                .postId(POST_ID)
                .build();
        userDto = new UserDto(AUTHOR_ID, "Ivan", "ivan@test.com");
    }

    @Test
    public void testCommentIsCreated() {
        when(commentMapper.toEntity(commentDto)).thenReturn(commentEntity);
        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(userDto);
        when(commentRepository.save(commentEntity)).thenReturn(savedCommentEntity);
        when(commentMapper.toDto(savedCommentEntity)).thenReturn(savedCommentDto);

        commentService.createComment(POST_ID, commentDto);

        verify(commentRepository, Mockito.times(1)).save(commentEntity);
    }

    @Test
    public void testCommentIsUpdated() {
        commentDto.setId(COMMENT_ID);
        savedCommentEntity.setContent(COMMENT_CONTENT);
        savedCommentDto.setContent(COMMENT_CONTENT);

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(savedCommentEntity));
        when(commentRepository.save(savedCommentEntity)).thenReturn(savedCommentEntity);
        when(commentMapper.toDto(savedCommentEntity)).thenReturn(savedCommentDto);

        CommentDto actualResult = commentService.updateComment(commentDto);

        assertEquals(COMMENT_CONTENT, actualResult.getContent());
    }

    @Test
    public void testDeleteComment() {
        Comment comment = Comment.builder()
                .id(3L)
                .build();
        long commentId = comment.getId();

        commentService.deleteComment(commentId);
        verify(commentRepository, Mockito.times(1)).deleteById(commentId);
    }

    @Test
    public void testDeleteCommentIncorrectId() {
        verify(commentRepository, Mockito.never()).deleteById(999L);
    }

    @Test
    public void testGetAllComments() {
        List<Comment> emptyEvents = new ArrayList<>();
        when(commentRepository.findAllByPostId(1L)).thenReturn(Collections.emptyList());

        Assertions.assertIterableEquals(emptyEvents, commentService.getAllComments(1L));
    }
}
