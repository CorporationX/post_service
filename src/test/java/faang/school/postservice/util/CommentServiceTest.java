package faang.school.postservice.util;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.messages.kafka.producers.CommentPublish;
import faang.school.postservice.messages.spring.publishers.SpringCommentPublisher;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.comment.CommentServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
    SpringCommentPublisher commentPublisher;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentPublish commentPublish;
    @InjectMocks
    CommentServiceImpl service;

    @Test
    public void sendComment_forbiddenException_shouldTrowEntityNotFoundException() {
        CreateCommentDto dto = CreateCommentDto.builder()
                .postId(2L)
                .content(" ")
                .build();
        when(postRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> service.createComment(dto));
    }

    @Test
    public void sendComment_responseDto_shouldResponseCommentDto() {
        CreateCommentDto dto = CreateCommentDto.builder()
                .postId(2L)
                .content("Test")
                .build();
        Post post = Post.builder()
                .id(2L)
                .build();
        Comment comment = Comment.builder()
                .post(post)
                .authorId(2L)
                .build();
        ResponseCommentDto responseDto = ResponseCommentDto.builder()
                .authorId(2L)
                .postId(2L)
                .build();
        when(commentMapper.toDto(comment)).thenReturn(responseDto);
        when(commentMapper.toEntity(dto)).thenReturn(comment);
        when(userContext.getUserId()).thenReturn(2L);
        when(postRepository.findById(2L)).thenReturn(Optional.of(post));
        when(commentRepository.save(comment)).thenReturn(comment);

        ResponseCommentDto responseCommentDto = service.createComment(dto);

        verify(commentRepository).save(comment);

        verify(commentPublisher).handleCommentCreated(any());
        Assertions.assertEquals(2L, responseCommentDto.postId());
    }

    @Test
    public void updateComment_trowEntity_shouldThrowEntityNotFoundException() {
        UpdateCommentDto dto = UpdateCommentDto.builder()
                .content("Test")
                .build();
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.updateComment(1L, dto));
    }

    @Test
    public void updateComment_trowForbidden_shouldThrowForbiddenException() {
        UpdateCommentDto dto = UpdateCommentDto.builder()
                .content("Test")
                .build();
        Comment comment = Comment.builder()
                .authorId(2L)
                .build();
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        Assertions.assertThrows(ForbiddenException.class,
                () -> service.updateComment(1L, dto));
    }

    @Test
    public void updateComment_updateComment_shouldThrowForbiddenException() {
        UpdateCommentDto dto = UpdateCommentDto.builder()
                .content("Test")
                .build();
        Comment comment = Comment.builder()
                .authorId(2L)
                .build();
        ResponseCommentDto responseDto = ResponseCommentDto.builder()
                .authorId(2L)
                .commentId(1L)
                .build();
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userContext.getUserId()).thenReturn(2L);
        when(commentMapper.toDto(comment)).thenReturn(responseDto);

        ResponseCommentDto responseCommentDto = service.updateComment(1L, dto);

        Assertions.assertEquals(1L, responseCommentDto.commentId());
    }

    @Test
    public void getComments_trowEntity_shouldThrowEntityNotFoundException() {
        when(postRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> service.getComments(1L, 2, 10));
    }

    @Test
    public void getComments_returnListDto_shouldListResponseCommentDto() {
        when(postRepository.existsById(1L)).thenReturn(true);
        Comment comment = Comment.builder()
                .authorId(2L)
                .build();
        Pageable pageable = PageRequest.of(2, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> commentsPage = new PageImpl<>(
                new ArrayList<>(List.of(comment)),
                PageRequest.of(2, 10),
                1
        );
        ResponseCommentDto responseDto = ResponseCommentDto.builder()
                .authorId(2L)
                .commentId(1L)
                .build();
        when(postRepository.findAllCommentByPostId(1L, pageable)).thenReturn(commentsPage);
        when(commentMapper.toDto(comment)).thenReturn(responseDto);

        List<ResponseCommentDto> listResponse = service.getComments(1L, 2, 10);

        Assertions.assertEquals(2L, listResponse.get(0).authorId());
        Assertions.assertEquals(1, listResponse.size());
    }

    @Test
    public void deleteComment_throwEntity_shouldEntityNotFoundException() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.deleteComment(1L));
    }

    @Test
    public void deleteComment_throwForbidden_shouldForbiddenException() {
        Comment comment = Comment.builder()
                .authorId(1L)
                .build();
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userContext.getUserId()).thenReturn(2L);
        assertThrows(ForbiddenException.class,
                () -> service.deleteComment(1L));
    }

    @Test
    public void deleteComment_deleteComment_shouldDeleteComment() {
        Comment comment = Comment.builder()
                .authorId(1L)
                .build();
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userContext.getUserId()).thenReturn(1L);

        service.deleteComment(1L);

        verify(commentRepository).delete(comment);
    }
}