package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.mapper.comment.CommentUpdateMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.test_data.TestDataComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {
    @Mock
    private CommentService commentService;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private CommentUpdateMapper commentUpdateMapper;
    @InjectMocks
    private CommentController commentController;
    private Post post;
    private Comment comment;
    private CommentDto commentDto;
    private CommentUpdateDto commentUpdateDto;

    @BeforeEach
    void setUp() {
        TestDataComment testDataComment = new TestDataComment();

        post = testDataComment.getPost();
        comment = testDataComment.getComment1();
        commentDto = testDataComment.getCommentDto1();
        commentUpdateDto = testDataComment.getCommentUpdateDto();
    }

    @Test
    void testCreateCommentSuccess() {
        when(commentMapper.toEntity(commentDto)).thenReturn(comment);
        when(commentService.createComment(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        CommentDto createdCommentDto = commentController.createComment(commentDto);

        assertEquals(commentDto, createdCommentDto);
        verify(commentMapper, atLeastOnce()).toEntity(commentDto);
        verify(commentService, atLeastOnce()).createComment(comment);
        verify(commentMapper, atLeastOnce()).toDto(comment);
    }

    @Test
    void testUpdateCommentSuccess() {
        when(commentUpdateMapper.toEntity(commentUpdateDto)).thenReturn(comment);
        when(commentService.updateComment(comment)).thenReturn(comment);
        when(commentUpdateMapper.toDto(comment)).thenReturn(commentUpdateDto);

        CommentUpdateDto updatedCommentDto = commentController.updateComment(commentUpdateDto);

        assertEquals(commentUpdateDto, updatedCommentDto);
        verify(commentUpdateMapper, atLeastOnce()).toEntity(commentUpdateDto);
        verify(commentService, atLeastOnce()).updateComment(comment);
        verify(commentUpdateMapper, atLeastOnce()).toDto(comment);
    }

    @Test
    void testDeleteCommentSuccess() {
        commentController.delete(comment.getId());

        verify(commentService, atLeastOnce()).deleteComment(comment.getId());
    }

    @Test
    void testFindAllCommentsSuccess() {
        List<Comment> commentList = List.of(comment);
        List<CommentDto> commentDtoList = List.of(commentDto);

        when(commentService.findAllComments(post.getId())).thenReturn(commentList);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        List<CommentDto> result = commentController.findAllComments(post.getId());
        assertEquals(commentDtoList, result);

        verify(commentService, atLeastOnce()).findAllComments(post.getId());
        verify(commentMapper, atLeastOnce()).toDto(comment);
    }
}