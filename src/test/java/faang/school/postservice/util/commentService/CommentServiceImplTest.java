package faang.school.postservice.util.commentService;


import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.Request.RequestCommentDto;
import faang.school.postservice.dto.comment.Response.ResponseCommentDto;
import faang.school.postservice.exception.ResourceNotFoundException;
import faang.school.postservice.mapper.comment.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.comment.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Spy
    private CommentMapperImpl commentMapper;

    private CommentService commentService;

    private Post testPost;
    private Comment testComment;
    private RequestCommentDto requestCommentDto;
    private ResponseCommentDto responseCommentDto;


    @BeforeEach
    void setUp() {
        commentService = new CommentServiceImpl(
            commentRepository,
            postRepository,
            commentMapper,
            new UserContext() {
                @Override
                public long getUserId() {
                    return 1L;
                }
            }
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
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        requestCommentDto = RequestCommentDto.builder()
                .content("Test Comment")
                .authorId(1L)
                .build();

        responseCommentDto = ResponseCommentDto.builder()
                .id(1L)
                .content("Test Comment")
                .authorId(1L)
                .postId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createComment_ShouldReturnResponseCommentDto_WhenValidRequest() throws ResourceNotFoundException {
        when(postRepository.findById(eq(1L))).thenReturn(Optional.of(testPost));
        when(commentMapper.toEntity(requestCommentDto)).thenReturn(testComment);
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
        when(commentMapper.toDto(testComment)).thenReturn(responseCommentDto);

        ResponseCommentDto result = commentService.createComment(requestCommentDto, 1L);

        assertNotNull(result);
        assertEquals(responseCommentDto.getId(), result.getId());
        assertEquals(responseCommentDto.getContent(), result.getContent());
        assertEquals(responseCommentDto.getAuthorId(), result.getAuthorId());
        assertEquals(responseCommentDto.getPostId(), result.getPostId());
        assertEquals(responseCommentDto.getCreatedAt(), result.getCreatedAt());
        assertEquals(responseCommentDto.getUpdatedAt(), result.getUpdatedAt());

        ArgumentCaptor<Comment> commentArgumentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository, times(1)).save(commentArgumentCaptor.capture());

        Comment capturedComment = commentArgumentCaptor.getValue();

        assertNotNull(capturedComment);
        assertEquals(testComment.getContent(), capturedComment.getContent());
        assertEquals(testComment.getAuthorId(), capturedComment.getAuthorId());
        assertEquals(testPost, capturedComment.getPost());

        verify(postRepository, times(1)).findById(1L);
        verify(commentMapper, times(1)).toEntity(requestCommentDto);
        verify(commentMapper, times(1)).toDto(testComment);
    }

    @Test
    void updateComment_ShouldReturnUpdatedResponseCommentDto_WhenValidRequest() throws ResourceNotFoundException {
        when(commentRepository.findById(any())).thenReturn(Optional.of(testComment));
        when(postRepository.findById(any())).thenReturn(Optional.of(testPost));
        when(commentRepository.save(any())).thenReturn(testComment);
        when(commentMapper.toDto(testComment)).thenReturn(responseCommentDto);

        ResponseCommentDto result = commentService.updateComment(1L, 1L, requestCommentDto);

        assertNotNull(result);
        assertEquals(responseCommentDto.getId(), result.getId());
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
        when(commentMapper.toDto(testComment)).thenReturn(responseCommentDto);

        List<ResponseCommentDto> result = commentService.getAllCommentsByPostId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(responseCommentDto.getId(), result.get(0).getId());
        assertEquals(responseCommentDto.getContent(), result.get(0).getContent());
        assertEquals(responseCommentDto.getAuthorId(), result.get(0).getAuthorId());
        assertEquals(responseCommentDto.getPostId(), result.get(0).getPostId());
        assertEquals(responseCommentDto.getCreatedAt(), result.get(0).getCreatedAt());
        assertEquals(responseCommentDto.getUpdatedAt(), result.get(0).getUpdatedAt());

        verify(postRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).findAllByPostId(1L);
        verify(commentMapper, times(1)).toDto(testComment);
    }
}