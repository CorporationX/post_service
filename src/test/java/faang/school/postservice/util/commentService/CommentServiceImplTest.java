package faang.school.postservice.util.commentService;


import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.request.RequestCreateComment;
import faang.school.postservice.dto.comment.request.RequestUpdateComment;
import faang.school.postservice.dto.comment.response.ResponseComment;
import faang.school.postservice.exception.ResourceNotFoundException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.publisher.EventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.comment.CommentServiceImpl;
import faang.school.postservice.validator.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentValidator commentValidator;

    @Spy
    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);


    private CommentService commentService;

    private Post testPost;
    private Comment testComment;
    private RequestCreateComment requestCreateComment;
    private RequestUpdateComment requestUpdateComment;
    private ResponseComment responseComment;
    @Mock
    private CommentEventPublisher commentEventPublisher;
    @Mock
    private EventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 1, 12, 0, 0);

        commentService = new CommentServiceImpl(
                commentRepository,
                postRepository,
                commentMapper,
                new UserContext() {
                    public Long getUserId() {
                        return 1L;
                    }
                },
                commentValidator,
                commentEventPublisher,
                eventPublisher
        );

        testPost = Post.builder()
                .id(1L)
                .content("Test Post")
                .authorId(1L)
                .build();

        testComment = Comment.builder()
                .id(1L)
                .content("Test Comment")
                .authorId(1L)
                .post(testPost)
                .createdAt(fixedTime)
                .updatedAt(fixedTime)
                .build();

        requestCreateComment = RequestCreateComment.builder()
                .content("Test Comment")
                .build();

        requestUpdateComment = RequestUpdateComment.builder()
                .content("Test Comment")
                .build();

        responseComment = ResponseComment.builder()
                .id(1L)
                .content("Test Comment")
                .authorId(1L)
                .postId(1L)
                .createdAt(fixedTime)
                .updatedAt(fixedTime)
                .build();
    }

    @Test
    void createComment_ShouldReturnResponseCommentDto_WhenValidRequest() throws ResourceNotFoundException {
        when(postRepository.findById(eq(1L))).thenReturn(Optional.of(testPost));
        when(commentMapper.toEntity(requestCreateComment)).thenReturn(testComment);
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
        when(commentMapper.toDto(testComment)).thenReturn(responseComment);

        ResponseComment result = commentService.createComment(requestCreateComment, 1L);

        assertNotNull(result);
        assertEquals(responseComment.id(), result.id());
        assertEquals(responseComment.content(), result.content());
        assertEquals(responseComment.authorId(), result.authorId());
        assertEquals(responseComment.postId(), result.postId());
        assertEquals(responseComment.createdAt(), result.createdAt());
        assertEquals(responseComment.updatedAt(), result.updatedAt());

        ArgumentCaptor<Comment> commentArgumentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository, times(1)).save(commentArgumentCaptor.capture());

        Comment capturedComment = commentArgumentCaptor.getValue();

        assertNotNull(capturedComment);
        assertEquals(testComment.getContent(), capturedComment.getContent());
        assertEquals(testComment.getAuthorId(), capturedComment.getAuthorId());
        assertEquals(testPost, capturedComment.getPost());

        verify(postRepository, times(1)).findById(1L);
        verify(commentMapper, times(1)).toEntity(requestCreateComment);
        verify(commentMapper, times(1)).toDto(testComment);

        verify(eventPublisher, times(1)).publish(argThat(event ->
                event.authorId() == testComment.getAuthorId() &&
                        event.postId() == testPost.getId() &&
                        event.commentId() == testComment.getId() &&
                        event.content().equals(testComment.getContent())
        ));

        verify(commentEventPublisher, times(1)).publish(
                eq(testComment.getAuthorId()),
                eq(testPost.getId()),
                eq(testComment.getId()),
                eq(testComment.getContent()),
                any(LocalDateTime.class)
        );
    }

    @Test
    void updateComment_ShouldReturnUpdatedResponseCommentDto_WhenValidRequest() throws ResourceNotFoundException {
        when(commentRepository.findById(any())).thenReturn(Optional.of(testComment));
        when(postRepository.findById(any())).thenReturn(Optional.of(testPost));
        when(commentRepository.save(any())).thenReturn(testComment);
        when(commentMapper.toDto(testComment)).thenReturn(responseComment);

        ResponseComment result = commentService.updateComment(1L, 1L, requestUpdateComment);

        assertNotNull(result);
        assertEquals(responseComment.id(), result.id());
        verify(commentRepository, times(1)).save(any());
        verify(commentMapper, times(1)).toDto(testComment);
    }

    @Test
    void deleteComment_ShouldNotThrowException_WhenValidRequest() throws ResourceNotFoundException {
        when(commentRepository.findById(any())).thenReturn(Optional.of(testComment));
        when(postRepository.findById(any())).thenReturn(Optional.of(testPost));

        assertDoesNotThrow(() -> commentService.deleteComment(1L, 1L));

        verify(commentRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).deleteById(1L);
    }

    @Test
    void getAllCommentsByPostId_ShouldReturnListOfResponseCommentDto_WhenValidRequest() throws ResourceNotFoundException {
        List<Comment> comments = new ArrayList<>();
        comments.add(testComment);

        when(postRepository.findById(eq(1L))).thenReturn(Optional.of(testPost));
        when(commentRepository.findAllByPostId(eq(1L))).thenReturn(comments);
        when(commentMapper.toDto(testComment)).thenReturn(responseComment);

        List<ResponseComment> result = commentService.getAllCommentsByPostId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(responseComment.id(), result.get(0).id());
        assertEquals(responseComment.content(), result.get(0).content());
        assertEquals(responseComment.authorId(), result.get(0).authorId());
        assertEquals(responseComment.postId(), result.get(0).postId());
        assertEquals(responseComment.createdAt(), result.get(0).createdAt());
        assertEquals(responseComment.updatedAt(), result.get(0).updatedAt());

        verify(postRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).findAllByPostId(1L);
        verify(commentMapper, times(1)).toDto(testComment);
    }
}