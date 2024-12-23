package faang.school.postservice.service;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentsServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;
    @InjectMocks
    private CommentService commentService;

    @Test
    void createComment_Success() {
        Long postId = 100L;
        Comment comment = creatTestComment();
        CommentDto commentDto = creatTestCommentDto();


        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDto);

        CommentDto result = commentService.createComment(postId,commentDto);

        verify(commentRepository).save(any(Comment.class));

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Test Comment");
        assertThat(result.getAuthorId()).isEqualTo(2L);
        assertThat(result.getPostId()).isEqualTo(postId);
        assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test
    void updateComment_Success() {
        Long commentId = 1L;
        Comment comment = creatTestComment();
        CommentDto updatedDto = creatTestCommentDto();
        updatedDto.setContent("Updated comment");

        when(commentRepository.findById(commentId)).thenReturn(java.util.Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toDto(any(Comment.class))).thenReturn(updatedDto);

        CommentDto result = commentService.updateComment(commentId, updatedDto);

        verify(commentRepository).findById(commentId);
        verify(commentRepository).save(any(Comment.class));

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Updated comment");
        assertThat(result.getAuthorId()).isEqualTo(2L);
    }

    @Test
    void getAllComments_Success() {
        Long postId = 100L;
        Comment comment = creatTestComment();
        CommentDto commentDto = creatTestCommentDto();

        when(commentRepository.findAllByPostId(postId)).thenReturn(List.of(comment));
        when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDto);

        List<CommentDto> result = commentService.getAllComments(postId);

        verify(commentRepository).findAllByPostId(postId);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("Test comment");
        assertThat(result.get(0).getPostId()).isEqualTo(postId);
    }

    @Test
    void deleteComment_Success() {
        Long commentId = 1L;

        when(commentRepository.existsById(commentId)).thenReturn(true);

        commentService.deleteCommentById(commentId);

        verify(commentRepository).existsById(commentId);
        verify(commentRepository).deleteById(commentId);
    }

    private CommentDto creatTestCommentDto() {
        return CommentDto.builder()
                .id(1L)
                .content("Test comment")
                .authorId(2L)
                .postId(100L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Comment creatTestComment() {
        return Comment.builder()
                .id(1L)
                .content("Test comment")
                .authorId(2L)

                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
