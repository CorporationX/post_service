package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.messaging.kafka.publishing.CommentProducer;
import faang.school.postservice.model.Comment;
import faang.school.postservice.moderation.ModerationDictionary;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validation.CommentValidator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ModerationDictionary moderationDictionary;
    @Mock
    private PostService postService;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private CommentService commentService;
    @Mock
    private CommentValidator commentValidator;
    @Spy
    private CommentMapper commentMapper;
    @Mock
    private CommentProducer commentProducer;

    @Test
    void testFindExistingComment_ExistingId() {
        long commentId = 1;
        Comment comment = new Comment();
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        Comment result = commentService.findExistingComment(commentId);

        assertNotNull(result);
        assertEquals(comment, result);
        verify(commentRepository).findById(commentId);
    }

    @Test
    void testFindExistingComment_NonExistingId() {
        long commentId = 1;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            commentService.findExistingComment(commentId);
        });
        verify(commentRepository).findById(commentId);
    }

    @Test
    void testGetUnverifiedComments() {
        List<Comment> unverifiedComments = new ArrayList<>();
        when(commentRepository.findByVerifiedDateBeforeAndVerifiedFalse(any(LocalDateTime.class)))
                .thenReturn(unverifiedComments);

        List<Comment> result = commentService.getUnverifiedComments();

        assertNotNull(result);
        assertEquals(unverifiedComments, result);
        verify(commentRepository).findByVerifiedDateBeforeAndVerifiedFalse(any(LocalDateTime.class));
    }

    @Test
    void testProcessCommentsBatch_ContainsBannedWords() {
        List<Comment> comments = new ArrayList<>();
        Comment comment1 = new Comment();
        comment1.setContent("Comment with word - horrifying");
        Comment comment2 = new Comment();
        comment2.setContent("Comment with word - shocking");
        comments.add(comment1);
        comments.add(comment2);

        when(moderationDictionary.containsBannedWord(anyString())).thenReturn(true);

        commentService.processCommentsBatch(comments);

        assertFalse(comment1.isVerified());
        assertFalse(comment2.isVerified());
        assertNotNull(comment1.getVerifiedDate());
        assertNotNull(comment2.getVerifiedDate());
        verify(commentRepository, times(2)).save(any(Comment.class));
    }


    @Test
    void testProcessCommentsBatch_NoBannedWords() {
        // Arrange
        List<Comment> comments = new ArrayList<>();
        Comment comment1 = new Comment();
        comment1.setContent("Comment without banned words");
        Comment comment2 = new Comment();
        comment2.setContent("Some comment");
        comments.add(comment1);
        comments.add(comment2);

        when(moderationDictionary.containsBannedWord(anyString())).thenReturn(false);

        commentService.processCommentsBatch(comments);

        assertTrue(comment1.isVerified());
        assertTrue(comment2.isVerified());
        assertNotNull(comment1.getVerifiedDate());
        assertNotNull(comment2.getVerifiedDate());
        verify(commentRepository, times(2)).save(any(Comment.class));
    }

    @Test
    public void testCreateComment_ValidComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setPostId(1L);

        Comment createdComment = mock(Comment.class);

        doNothing().when(commentValidator).validateAuthorExist(any(CommentDto.class));
        doNothing().when(commentValidator).validateCommentBeforeCreate(any(CommentDto.class));
        when(commentMapper.toEntity(any(CommentDto.class)))
                .thenReturn(createdComment);
        when(commentRepository.save(any(Comment.class))).thenReturn(createdComment);
        when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDto);

        CommentDto result = commentService.createComment(commentDto);

        assertNotNull(result);
        assertEquals(commentDto, result);
    }

    @Test
    public void testCreateComment_InvalidAuthor() {
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorId(2L);

        doThrow(DataValidationException.class).when(commentValidator).validateAuthorExist(commentDto);

        assertThrows(DataValidationException.class, () -> commentService.createComment(commentDto));
    }

    @Test
    public void testUpdateComment_ValidComment() {
        Long commentId = 1L;
        CommentDto commentDto = new CommentDto();

        Comment existingComment = new Comment();

        doNothing().when(commentValidator).validateAuthorExist(any(CommentDto.class));
        doNothing().when(commentValidator).validateCommentBeforeUpdate(anyLong(),any(CommentDto.class));
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(existingComment));
        when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(existingComment);
        when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDto);

        CommentDto result = commentService.updateComment(commentId, commentDto);

        assertEquals(commentDto, result);
    }

    @Test
    public void testUpdateComment_InvalidAuthor() {
        Long commentId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorId(2L);

        doThrow(DataValidationException.class).when(commentValidator).validateAuthorExist(commentDto);

        assertThrows(DataValidationException.class, () -> commentService.updateComment(commentId, commentDto));
    }

    @Test
    public void testGetCommentsByPostId_ValidPost() {
        Long postId = 1L;

        List<Comment> mockComments = new ArrayList<>();
        mockComments.add(new Comment());
        mockComments.add(new Comment());

        List<CommentDto> mockCommentDto = new ArrayList<>();
        mockCommentDto.add(new CommentDto());
        mockCommentDto.add(new CommentDto());

        doNothing().when(commentValidator).validateCommentBeforeGetCommentsByPostId(anyLong());
        when(commentRepository.findAllByPostIdOrderByCreatedAtDesc(postId)).thenReturn(mockComments);
        when(commentMapper.toDtoList(anyList())).thenReturn(mockCommentDto);

        List<CommentDto> result = commentService.getCommentsByPostId(postId);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(commentMapper, times(1)).toDtoList(anyList());
    }

    @Test
    public void testGetCommentsByPostId_InvalidPost() {
        Long postId = 2L;

        doThrow(EntityNotFoundException.class).when(commentValidator)
                .validateCommentBeforeGetCommentsByPostId(postId);

        assertThrows(EntityNotFoundException.class, () -> commentService.getCommentsByPostId(postId));
    }

    @Test
    public void testDeleteComment_ValidComment() {
        Long commentId = 1L;

        Comment existingComment = new Comment();
        CommentDto commentDto = new CommentDto();

        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(existingComment));

        when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDto);

        CommentDto result = commentService.deleteComment(commentId);

        assertEquals(commentDto, result);
    }

    @Test
    public void testDeleteComment_InvalidComment() {
        Long commentId = 2L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.deleteComment(commentId));
    }
}
