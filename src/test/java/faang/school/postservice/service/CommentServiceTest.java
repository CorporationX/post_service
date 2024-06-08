package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validator.comment.CommentValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Spy
    private CommentMapperImpl commentMapper;

    @Mock
    private CommentValidator commentValidator;

    @InjectMocks
    private CommentService commentService;

    @Test
    public void testCommentIsCreated() {
        CommentDto commentDto = new CommentDto();
        Comment commentEntity = commentMapper.toEntity(commentDto);
        Mockito.when(commentMapper.toEntity(commentDto)).thenReturn(commentEntity);
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(new Post()));
        commentService.createComment(1L, commentDto);
        Mockito.verify(commentRepository, Mockito.times(1)).save(commentEntity);
    }

    @Test
    public void testCommentIsUpdated() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .build();
        Comment commentEntity = commentMapper.toEntity(commentDto);
        Mockito.when(commentRepository.findById(1L)).thenReturn(Optional.of(commentEntity));
        commentService.updateComment(commentDto);
        Mockito.verify(commentRepository, Mockito.times(1)).save(commentEntity);
    }

    @Test
    public void testDeleteComment() {
        Comment comment = Comment.builder()
                .id(3L)
                .build();
        long commentId = comment.getId();

        commentService.deleteComment(commentId);
        Mockito.verify(commentRepository, Mockito.times(1)).deleteById(commentId);
    }

    @Test
    public void testDeleteCommentIncorrectId() {
        Mockito.verify(commentRepository, Mockito.never()).deleteById(999L);
    }

    @Test
    public void testGetAllComments() {
        List<Comment> emptyEvents = new ArrayList<>();
        Mockito.when(commentRepository.findAllByPostId(1L)).thenReturn(Collections.emptyList());

        Assertions.assertIterableEquals(emptyEvents, commentService.getAllComments(1L));
    }
}
