package faang.school.postservice.service;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.redis.CommentEventDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;
    @Mock
    private PostService postService;
    @Mock
    private CommentEventPublisher commentEventPublisher;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private CommentValidator commentValidator;
    @Mock
    CommentRepository commentRepository;

    @Test
    void testCreateComment_ValidData_ReturnsCreatedCommentDto() {
        Long postId = 1L;
        CommentDto commentDto = CommentDto.builder()
                .content("Test comment")
                .authorId(2L)
                .postId(postId)
                .build();
        Comment commentEntity = mock(Comment.class);
        Post post = Post.builder().id(postId).authorId(2L).build();

        when(commentMapper.toEntity(commentDto, postId)).thenReturn(commentEntity);
        when(commentRepository.save(commentEntity)).thenReturn(commentEntity);
        when(commentMapper.toDto(commentEntity)).thenReturn(commentDto);
        when(postService.getPostById(postId)).thenReturn(post);

        CommentDto result = commentService.createComment(postId, commentDto);
        
        assertNotNull(result);
        assertEquals(commentDto.getContent(), result.getContent());
        assertEquals(commentDto.getAuthorId(), result.getAuthorId());
        assertEquals(commentDto.getPostId(), result.getPostId());

        verify(commentValidator).validateUserBeforeCreate(commentDto);
        verify(commentMapper).toEntity(commentDto, postId);
        verify(commentRepository).save(commentEntity);
        verify(commentMapper).toDto(commentEntity);
        verify(postService).getPostById(postId);
        verify(commentEventPublisher).publish(any(CommentEventDto.class));
    }


    @Test
    void testUpdateComment_ValidData_ReturnsUpdatedCommentDto() {
        long commentId = 1L;
        CommentDto commentDto = CommentDto.builder()
                .id(commentId)
                .content("Updated comment content")
                .authorId(2L)
                .postId(3L)
                .build();

        Comment existingComment = Comment.builder()
                .id(commentId)
                .content("Original comment content")
                .authorId(2L)
                .post(Post.builder().id(3L).build())
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        commentService.updateComment(commentId, commentDto);

        verify(commentRepository, times(1)).findById(commentId);
        verify(commentValidator).validateBeforeUpdate(existingComment, commentDto);
        verify(commentMapper).partialUpdate(commentDto, existingComment);
        verify(commentMapper, times(1)).toDto(existingComment);
    }

    @Test
    public void testUpdateComment_CommentNotFound_ThrowsEntityNotFoundException() {
        long commentId = 1L;
        CommentDto commentDto = CommentDto.builder()
                .id(commentId)
                .content("Updated comment content")
                .authorId(2L)
                .postId(3L)
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> commentService.updateComment(commentId, commentDto));

        assertEquals("Comment with id 1 not found", exception.getMessage());
    }

    @Test
    void testGetCommentsByPostId_ValidPostId_ReturnsSortedCommentDtoList() {
        long postId = 1L;

        Comment comment1 = Comment.builder().id(1L)
                .content("Comment 1")
                .createdAt(LocalDateTime.now().minusHours(3)).build();
        Comment comment2 = Comment.builder().id(2L)
                .content("Comment 2")
                .createdAt(LocalDateTime.now().minusHours(2)).build();

        List<Comment> comments = List.of(comment1, comment2);

        CommentDto commentDto1 = CommentDto.builder().id(1L)
                .content("Comment 1").build();
        CommentDto commentDto2 = CommentDto.builder().id(2L)
                .content("Comment 2").build();

        when(commentRepository.findAllByPostIdOrderByCreatedAtDesc(postId)).thenReturn(comments);
        when(commentMapper.toDto(comment1)).thenReturn(commentDto1);
        when(commentMapper.toDto(comment2)).thenReturn(commentDto2);

        List<CommentDto> result = commentService.getCommentsByPostId(postId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(commentDto1, result.get(0));
        assertEquals(commentDto2, result.get(1));

        verify(commentRepository, times(1)).findAllByPostIdOrderByCreatedAtDesc(postId);
        verify(commentMapper, times(1)).toDto(comment1);
        verify(commentMapper, times(1)).toDto(comment2);
    }

    @Test
    void testGetCommentsByPostId_InvalidPostId_ReturnsEmptyList() {
        long postId = 9L;

        when(commentRepository.findAllByPostIdOrderByCreatedAtDesc(postId)).thenReturn(Collections.emptyList());

        List<CommentDto> result = commentService.getCommentsByPostId(postId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(commentRepository, times(1)).findAllByPostIdOrderByCreatedAtDesc(postId);
        verifyNoMoreInteractions(commentMapper);
    }

    @Test
    void testDeleteComment_ExistingCommentId_DeletesComment() {
        long commentId = 1L;
        
        commentService.deleteComment(commentId);

        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    void testSendCommentEvent() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .authorId(2L)
                .content("Test comment")
                .postId(3L)
                .build();

        Post post = Post.builder()
                .id(3L)
                .authorId(1L)
                .build();

        when(postService.getPostById(commentDto.getPostId())).thenReturn(post);

        commentService.sendCommentEvent(commentDto);

        verify(postService).getPostById(commentDto.getPostId());

        CommentEventDto expectedEventDto = CommentEventDto.builder()
                .idComment(commentDto.getId())
                .authorIdComment(commentDto.getAuthorId())
                .postId(commentDto.getPostId())
                .postAuthorId(post.getAuthorId())
                .contentComment(commentDto.getContent())
                .build();

        verify(commentEventPublisher).publish(expectedEventDto);
        verify(commentEventPublisher, times(1)).publish(expectedEventDto);
    }
}