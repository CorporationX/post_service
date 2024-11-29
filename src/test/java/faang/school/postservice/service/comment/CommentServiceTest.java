package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentNewsFeedDto;
import faang.school.postservice.exception.ValidationException;
import faang.school.postservice.exception.comment.CommentNotFoundException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.kafka.KafkaEventProducer;
import faang.school.postservice.publisher.kafka.events.PostCommentEvent;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.redis.CachedAuthorService;
import faang.school.postservice.service.redis.CachedPostService;
import faang.school.postservice.validator.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentValidator commentValidator;
    @Mock
    private PostService postService;
    @Mock
    private CachedPostService cachedPostService;
    @Mock
    private CachedAuthorService cachedAuthorService;
    @Mock
    private KafkaEventProducer kafkaEventProducer;
    @Mock
    private CommentMapper commentMapper;
    @Captor
    private ArgumentCaptor<Comment> commentCaptor;
    @InjectMocks
    private CommentService commentService;

    private static final Long postId = 1L;
    private static final Long commentId = 1L;
    private static final Long authorId = 1L;
    private static final Long invalidAuthorId = 2L;
    private static final String content = "content";
    private static final String newContent = "new content";
    private Comment found;
    private Comment newComment;

    @BeforeEach
    void init() {
        found = Comment.builder().id(commentId).content(content).authorId(authorId).build();
        newComment = Comment.builder().content(newContent).authorId(authorId).build();
    }

    @Test
    void testCreateComment() {
        Long postAuthorId = 3L;
        Post post = Post.builder().id(postId).authorId(postAuthorId).build();
        Comment comment = Comment.builder().authorId(authorId).content(content).build();
        Comment savedComment = Comment.builder().id(10L).authorId(authorId).content(content).post(post).build();
        CommentNewsFeedDto commentNewsFeedDto = CommentNewsFeedDto.builder().id(10L).content(content).build();

        when(postService.findPostById(postId)).thenReturn(post);
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);
        when(commentMapper.toNewsFeedDto(savedComment)).thenReturn(commentNewsFeedDto);

        Comment result = commentService.createComment(postId, comment);

        verify(commentValidator).validateCreate(postId, comment);
        verify(postService).findPostById(postId);
        verify(commentRepository).save(comment);
        verify(cachedPostService).addCommentToCachedPost(postId, commentNewsFeedDto);
        verify(cachedAuthorService).saveAuthorCache(authorId);

        ArgumentCaptor<PostCommentEvent> captor = ArgumentCaptor.forClass(PostCommentEvent.class);
        verify(kafkaEventProducer).sendEvent(captor.capture());

        PostCommentEvent capturedEvent = captor.getValue();
        assertEquals(commentNewsFeedDto, capturedEvent.getCommentNewsFeedDto());
        assertEquals(commentNewsFeedDto.getId(), capturedEvent.getCommentNewsFeedDto().getId());
        assertEquals(commentNewsFeedDto.getContent(), capturedEvent.getCommentNewsFeedDto().getContent());

        assertEquals(savedComment, result);
    }

    @Test
    void testCreateCommentThrowsValidationException() {
        doThrow(new ValidationException("error"))
                .when(commentValidator).validateCreate(postId, newComment);

        assertThrows(
                ValidationException.class,
                () -> commentService.createComment(postId, newComment)
        );

        verify(commentValidator).validateCreate(postId, newComment);
        verifyNoInteractions(postService, commentRepository, cachedPostService, kafkaEventProducer);
    }

    @Test
    void testUpdateComment() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.ofNullable(found));

        commentService.updateComment(commentId, newComment);

        verify(commentValidator).validateCommentAuthorId(authorId, found);
        verify(commentRepository).save(commentCaptor.capture());

        Comment savedComment = commentCaptor.getValue();
        assertEquals(commentId, savedComment.getId());
        assertEquals(newContent, savedComment.getContent());
    }

    @Test
    void testUpdateCommentThrowsValidationException() {
        newComment.setAuthorId(invalidAuthorId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.ofNullable(found));
        doThrow(ValidationException.class)
                .when(commentValidator).validateCommentAuthorId(invalidAuthorId, found);

        assertThrows(
                ValidationException.class,
                () -> commentService.updateComment(commentId, newComment)
        );

        verify(commentValidator).validateCommentAuthorId(invalidAuthorId, found);
    }

    @Test
    void testGetAllCommentsByPostId() {
        var comment1 = Comment.builder()
                .id(1L)
                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 1, 1))
                .build();
        var comment2 = Comment.builder()
                .id(2L)
                .createdAt(LocalDateTime.of(2024, 2, 1, 1, 1, 1))
                .build();
        var comment3 = Comment.builder()
                .id(3L)
                .createdAt(LocalDateTime.of(2024, 3, 1, 1, 1, 1))
                .build();
        List<Comment> comments = List.of(comment1, comment2, comment3);
        List<Comment> sortedComments = comments.stream()
                .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                .toList();

        when(commentRepository.findAllByPostId(postId)).thenReturn(comments);

        var actual = commentService.getAllCommentsByPostId(postId);

        assertEquals(sortedComments, actual);
    }

    @Test
    void testDelete() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.ofNullable(found));

        commentService.delete(commentId);

        verify(commentRepository).deleteById(commentId);
    }

    @Test
    void testDeleteThrowsCommentNotFoundException() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(
                CommentNotFoundException.class,
                () -> commentService.delete(commentId)
        );

        verify(commentRepository).findById(commentId);
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void testGetById() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.ofNullable(found));

        var actual = commentService.getById(commentId);

        assertEquals(found, actual);
    }

    @Test
    void testGetByIdThrowsCommentNotFoundException() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(
                CommentNotFoundException.class,
                () -> commentService.getById(commentId)
        );

        verify(commentRepository).findById(commentId);
    }
}