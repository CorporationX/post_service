package faang.school.postservice.controller;

import static org.mockito.Mockito.*;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {
    @InjectMocks
    private CommentController commentController;
    @Mock
    private CommentService commentService;


    CommentDto commentDto;
    @BeforeEach
    public void setUp() {
        commentDto = CommentDto.builder()
                .authorId(1L)
                .id(1L)
                .content("content")
                .createdAt(LocalDateTime.now())
                .build();
        Post post = new Post();
    }
    @Test
    public void testCreateVerify() {
        commentController.create(commentDto, 1L);
        verify(commentService, times(1)).create(commentDto, 1L);
    }

    @Test
    public void testUpdateVerify() {
        commentController.update(commentDto, 1L);
        verify(commentService, times(1)).update(commentDto, 1L);
    }

    @Test
    public void testDeleteVerify() {
        commentController.delete(commentDto, 1L);
        verify(commentService, times(1)).delete(commentDto, 1L);
    }

    @Test
    public void testGetAllCommentsByPostIdVerify() {
        commentController.getAllCommentsByPostId(1L);
        verify(commentService, times(1)).getAllCommentsByPostId(1L);
    }
}
