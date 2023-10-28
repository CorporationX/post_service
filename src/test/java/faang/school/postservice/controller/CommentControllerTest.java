package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;
    @InjectMocks
    private CommentController commentController;

    private CommentDto commentDto;
    private Comment comment;
    private Post post;
    private final Long COMMENT_ID = 1L;
    private final Long POST_ID = 1L;

    @BeforeEach
    void initData() {
        Post post = Post.builder()
                .id(1L)
                .build();
        comment = Comment.builder()
                .id(1L)
                .authorId(1L)
                .post(post)
                .content("comment")
                .build();
        commentDto = CommentDto.builder()
                .id(1L)
                .authorId(1L)
                .postId(POST_ID)
                .content("comment")
                .build();
    }

    @Test
    void testCreateComment() {
        commentController.createComment(commentDto);
        verify(commentService).createComment(commentDto);
    }

    @Test
    void testGetComment() {
        commentController.getComment(COMMENT_ID);
        verify(commentService).getComment(COMMENT_ID);
    }

    @Test
    void testDeleteComment() {
        commentController.deleteComment(COMMENT_ID);
        verify(commentService).deleteComment(COMMENT_ID);
    }

    @Test
    void testUpdateComment() {
        commentController.updateComment(commentDto);
        verify(commentService).updateComment(commentDto);
    }

    @Test
    void testGetAllComments() {
        commentController.getAllComments(POST_ID);
        verify(commentService).getAllComments(POST_ID);
    }
}