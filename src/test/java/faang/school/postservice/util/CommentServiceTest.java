package faang.school.postservice.util;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.dto.comment.SendCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.comment.CommentServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private UserContext userContext;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private PostRepository postRepository;
    @InjectMocks
    CommentServiceImpl service;

    @Captor
    ArgumentCaptor<Comment> captor;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    @Test
    public void sendComment_forbiddenException_shouldTrowForbiddenException() {
        SendCommentDto dto = SendCommentDto.builder()
                .authorId(1L)
                .postId(2L)
                .content(" ")
                .build();
        assertThrows(ForbiddenException.class,
                () -> service.sendComment(dto));
    }

    @Test
    public void sendComment_entityException_shouldThrowEntityNotFoundException() {
        SendCommentDto dto = SendCommentDto.builder()
                .authorId(1L)
                .postId(2L)
                .content(" ")
                .build();
        when(userContext.getUserId()).thenReturn(1L);
        when(postRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> service.sendComment(dto));
    }

    @Test
    public void sendComment_sendComment_shouldCreateAndSaveComment() {
        SendCommentDto dto = SendCommentDto.builder()
                .authorId(1L)
                .postId(2L)
                .content("test")
                .build();
        Post post = new Post();
        Comment comment = new Comment();
        when(userContext.getUserId()).thenReturn(1L);
        when(postRepository.findById(2L)).thenReturn(Optional.of(post));
        when(commentMapper.toEntity(dto)).thenReturn(comment);

        service.sendComment(dto);

        verify(commentRepository, times(1)).save(captor.capture());
        Comment commentCapture = captor.getValue();
        Assertions.assertEquals(post, commentCapture.getPost());
        Assertions.assertNotNull(commentCapture.getCreatedAt());
    }

    @Test
    public void updateComment_forbiddenException_shouldTrowForbiddenException() {
        UpdateCommentDto dto = UpdateCommentDto.builder()
                .authorId(1L)
                .commentId(10L)
                .content("test")
                .build();
        assertThrows(ForbiddenException.class,
                () -> service.updateComment(dto, 1L));
    }

    @Test
    public void updateComment_entityException_shouldTrowEntityNotFoundException() {
        UpdateCommentDto dto = UpdateCommentDto.builder()
                .authorId(1L)
                .commentId(10L)
                .content("test")
                .build();
        when(userContext.getUserId()).thenReturn(1L);
        when(commentRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> service.updateComment(dto, 1L));
    }

    @Test
    public void updateComment_forbiddenException_shouldThrowForbiddenException() {
        UpdateCommentDto dto = UpdateCommentDto.builder()
                .authorId(1L)
                .commentId(10L)
                .content("test")
                .build();
        Comment comment = new Comment();
        Post post = Post.builder()
                .id(2L)
                .build();
        comment.setPost(post);
        when(userContext.getUserId()).thenReturn(1L);
        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));
        assertThrows(ForbiddenException.class,
                () -> service.updateComment(dto, 1L));
    }

    @Test
    public void updateComment_updateComment_shouldSetContentAndUpdateAt() {
        UpdateCommentDto dto = UpdateCommentDto.builder()
                .authorId(1L)
                .commentId(10L)
                .content("test")
                .build();
        long postId = 2L;
        Comment comment = spy(new Comment());
        Post post = Post.builder().id(postId).build();
        comment.setPost(post);

        when(userContext.getUserId()).thenReturn(1L);
        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));

        service.updateComment(dto, postId);
        verify(comment).setContent("test");
        verify(comment).setUpdatedAt(any(LocalDateTime.class));

        Assertions.assertEquals("test", comment.getContent());
        Assertions.assertNotNull(comment.getUpdatedAt());
    }

    @Test
    public void getComments_nonExist_shouldThrowEntityNotFoundException() {
        when(postRepository.existsById(1L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class,
                () -> service.getComments(1L, 1, 10));
    }

    @Test
    public void getComments_responseListDto_shouldResponseList() {
        long postId = 1L;
        int page = 0;
        int pageSize = 10;
        List<Comment> comments = List.of(
                Comment.builder().id(1L).content("Comment 1").createdAt(LocalDateTime.now().minusDays(1)).build(),
                Comment.builder().id(2L).content("Comment 2").createdAt(LocalDateTime.now().minusDays(2)).build()
        );
        Page<Comment> pageable = new PageImpl<>(
                comments,
                PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt")),
                comments.size()
        );

        when(postRepository.existsById(postId)).thenReturn(true);
        when(postRepository.findAllCommentByPostId(eq(postId), any(Pageable.class))).thenReturn(pageable);
        when(commentMapper.toDto(any(Comment.class))).thenAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            return ResponseCommentDto.builder()
                    .commentId(comment.getId())
                    .content(comment.getContent())
                    .build();
        });

        List<ResponseCommentDto> result = service.getComments(postId, page, pageSize);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Comment 1", result.get(0).content());
        Assertions.assertEquals("Comment 2", result.get(1).content());

        verify(postRepository).findAllCommentByPostId(eq(postId), pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        Assertions.assertEquals(page, capturedPageable.getPageNumber());
        Assertions.assertEquals(pageSize, capturedPageable.getPageSize());
    }
}