package faang.school.postservice.service.comment;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.junit.jupiter.api.AfterEach;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentServiceImpl Tests")
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private CommentEventPublisher commentEventPublisher;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Captor
    private ArgumentCaptor<Comment> commentCaptor;

    @Captor
    private ArgumentCaptor<CommentEvent> eventCaptor;

    private static final Long USER_ID = 1L;
    private static final Long POST_ID = 10L;
    private static final Long COMMENT_ID = 100L;
    private static final Long POST_AUTHOR_ID = 2L;

    @BeforeEach
    void setUp() {
        TransactionSynchronizationManager.initSynchronization();
    }

    @AfterEach
    void tearDown() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clear();
        }
    }

    // ==================== createComment() ====================

    @Test
    @DisplayName("Should create comment successfully when post exists")
    void createComment_WithValidPost_ShouldCreateComment() {
        // Arrange
        CommentDto commentDto = CommentDto.builder()
                .content("Test comment")
                .postId(POST_ID)
                .build();

        Post post = new Post();
        post.setId(POST_ID);
        post.setAuthorId(POST_AUTHOR_ID);

        Comment comment = new Comment();
        comment.setId(COMMENT_ID);
        comment.setContent("Test comment");
        comment.setAuthorId(USER_ID);
        comment.setPost(post);

        CommentDto expectedDto = CommentDto.builder()
                .id(COMMENT_ID)
                .content("Test comment")
                .authorId(USER_ID)
                .postId(POST_ID)
                .build();

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(commentMapper.toEntity(commentDto)).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(expectedDto);

        // Act
        CommentDto result = commentService.createComment(commentDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(COMMENT_ID);
        assertThat(result.getContent()).isEqualTo("Test comment");
        assertThat(result.getAuthorId()).isEqualTo(USER_ID);
        assertThat(result.getPostId()).isEqualTo(POST_ID);

        verify(userContext, times(1)).getUserId();
        verify(postRepository, times(1)).findById(POST_ID);
        verify(commentMapper, times(1)).toEntity(commentDto);
        verify(commentRepository, times(1)).save(commentCaptor.capture());
        verify(commentMapper, times(1)).toDto(comment);

        Comment savedComment = commentCaptor.getValue();
        assertThat(savedComment.getAuthorId()).isEqualTo(USER_ID);
        assertThat(savedComment.getPost()).isEqualTo(post);
    }

    @Test
    @DisplayName("Should register event publishing when comment author is different from post author")
    void createComment_WithDifferentAuthor_ShouldRegisterEventPublishing() {
        // Arrange
        CommentDto commentDto = CommentDto.builder()
                .content("Test comment")
                .postId(POST_ID)
                .build();

        Post post = new Post();
        post.setId(POST_ID);
        post.setAuthorId(POST_AUTHOR_ID);

        Comment comment = new Comment();
        comment.setId(COMMENT_ID);
        comment.setContent("Test comment");
        comment.setAuthorId(USER_ID);
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());

        CommentDto expectedDto = CommentDto.builder()
                .id(COMMENT_ID)
                .content("Test comment")
                .authorId(USER_ID)
                .postId(POST_ID)
                .build();

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(commentMapper.toEntity(commentDto)).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(expectedDto);

        // Act
        CommentDto result = commentService.createComment(commentDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(COMMENT_ID);
        // Verify
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("Should not publish event when comment author is same as post author")
    void createComment_WithSameAuthor_ShouldNotPublishEvent() {
        // Arrange
        CommentDto commentDto = CommentDto.builder()
                .content("Test comment")
                .postId(POST_ID)
                .build();

        Post post = new Post();
        post.setId(POST_ID);
        post.setAuthorId(USER_ID); // Same as comment author

        Comment comment = new Comment();
        comment.setId(COMMENT_ID);
        comment.setContent("Test comment");
        comment.setAuthorId(USER_ID);
        comment.setPost(post);

        CommentDto expectedDto = CommentDto.builder()
                .id(COMMENT_ID)
                .content("Test comment")
                .authorId(USER_ID)
                .postId(POST_ID)
                .build();

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(commentMapper.toEntity(commentDto)).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(expectedDto);

        // Act
        commentService.createComment(commentDto);

        // Assert
        verify(commentEventPublisher, never()).publish(any(CommentEvent.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when post does not exist")
    void createComment_WithNonExistentPost_ShouldThrowException() {
        // Arrange
        CommentDto commentDto = CommentDto.builder()
                .content("Test comment")
                .postId(POST_ID)
                .build();

        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> commentService.createComment(commentDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Post with id " + POST_ID + " not found");

        verify(postRepository, times(1)).findById(POST_ID);
        verify(commentRepository, never()).save(any(Comment.class));
        verify(commentEventPublisher, never()).publish(any(CommentEvent.class));
    }

    // ==================== updateComment() ====================

    @Test
    @DisplayName("Should update comment successfully when user is author")
    void updateComment_WithAuthorUser_ShouldUpdateComment() {
        // Arrange
        CommentDto commentDto = CommentDto.builder()
                .content("Updated content")
                .build();

        Comment existingComment = new Comment();
        existingComment.setId(COMMENT_ID);
        existingComment.setContent("Old content");
        existingComment.setAuthorId(USER_ID);

        CommentDto expectedDto = CommentDto.builder()
                .id(COMMENT_ID)
                .content("Updated content")
                .authorId(USER_ID)
                .build();

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(existingComment));
        when(commentMapper.toDto(existingComment)).thenReturn(expectedDto);

        // Act
        CommentDto result = commentService.updateComment(COMMENT_ID, commentDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Updated content");
        assertThat(existingComment.getContent()).isEqualTo("Updated content");

        verify(userContext, times(1)).getUserId();
        verify(commentRepository, times(1)).findById(COMMENT_ID);
        verify(commentMapper, times(1)).toDto(existingComment);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when user is not author")
    void updateComment_WithNonAuthorUser_ShouldThrowException() {
        // Arrange
        CommentDto commentDto = CommentDto.builder()
                .content("Updated content")
                .build();

        Comment existingComment = new Comment();
        existingComment.setId(COMMENT_ID);
        existingComment.setContent("Old content");
        existingComment.setAuthorId(999L); // Different author

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(existingComment));

        // Act & Assert
        assertThatThrownBy(() -> commentService.updateComment(COMMENT_ID, commentDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("You can only modify your own comments");

        verify(userContext, times(1)).getUserId();
        verify(commentRepository, times(1)).findById(COMMENT_ID);
        verify(commentMapper, never()).toDto(any(Comment.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when comment does not exist")
    void updateComment_WithNonExistentComment_ShouldThrowException() {
        // Arrange
        CommentDto commentDto = CommentDto.builder()
                .content("Updated content")
                .build();

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> commentService.updateComment(COMMENT_ID, commentDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Comment with id " + COMMENT_ID + " not found");

        verify(commentRepository, times(1)).findById(COMMENT_ID);
        verify(commentMapper, never()).toDto(any(Comment.class));
    }

    // ==================== deleteComment() ====================

    @Test
    @DisplayName("Should delete comment successfully when user is author")
    void deleteComment_WithAuthorUser_ShouldDeleteComment() {
        // Arrange
        Comment existingComment = new Comment();
        existingComment.setId(COMMENT_ID);
        existingComment.setContent("Test content");
        existingComment.setAuthorId(USER_ID);

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(existingComment));
        doNothing().when(commentRepository).delete(existingComment);

        // Act
        commentService.deleteComment(COMMENT_ID);

        // Assert
        verify(userContext, times(1)).getUserId();
        verify(commentRepository, times(1)).findById(COMMENT_ID);
        verify(commentRepository, times(1)).delete(existingComment);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when user is not author")
    void deleteComment_WithNonAuthorUser_ShouldThrowException() {
        // Arrange
        Comment existingComment = new Comment();
        existingComment.setId(COMMENT_ID);
        existingComment.setAuthorId(999L); // Different author

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(existingComment));

        // Act & Assert
        assertThatThrownBy(() -> commentService.deleteComment(COMMENT_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("You can only modify your own comments");

        verify(userContext, times(1)).getUserId();
        verify(commentRepository, times(1)).findById(COMMENT_ID);
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when comment does not exist")
    void deleteComment_WithNonExistentComment_ShouldThrowException() {
        // Arrange
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> commentService.deleteComment(COMMENT_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Comment with id " + COMMENT_ID + " not found");

        verify(commentRepository, times(1)).findById(COMMENT_ID);
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    // ==================== getCommentById() ====================

    @Test
    @DisplayName("Should return comment DTO when comment exists")
    void getCommentById_WithExistentComment_ShouldReturnDto() {
        // Arrange
        Comment comment = new Comment();
        comment.setId(COMMENT_ID);
        comment.setContent("Test content");
        comment.setAuthorId(USER_ID);

        CommentDto expectedDto = CommentDto.builder()
                .id(COMMENT_ID)
                .content("Test content")
                .authorId(USER_ID)
                .build();

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(commentMapper.toDto(comment)).thenReturn(expectedDto);

        // Act
        CommentDto result = commentService.getCommentById(COMMENT_ID);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(COMMENT_ID);
        assertThat(result.getContent()).isEqualTo("Test content");
        assertThat(result.getAuthorId()).isEqualTo(USER_ID);

        verify(commentRepository, times(1)).findById(COMMENT_ID);
        verify(commentMapper, times(1)).toDto(comment);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when comment does not exist")
    void getCommentById_WithNonExistentComment_ShouldThrowException() {
        // Arrange
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> commentService.getCommentById(COMMENT_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Comment with id " + COMMENT_ID + " not found");

        verify(commentRepository, times(1)).findById(COMMENT_ID);
        verify(commentMapper, never()).toDto(any(Comment.class));
    }

    // ==================== getCommentsByPostId() ====================

    @Test
    @DisplayName("Should return page of comments when post exists")
    void getCommentsByPostId_WithExistentPost_ShouldReturnPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        Comment comment1 = new Comment();
        comment1.setId(COMMENT_ID);
        comment1.setContent("Comment 1");
        comment1.setAuthorId(USER_ID);

        Comment comment2 = new Comment();
        comment2.setId(COMMENT_ID + 1);
        comment2.setContent("Comment 2");
        comment2.setAuthorId(USER_ID + 1);

        List<Comment> comments = List.of(comment1, comment2);
        Page<Comment> commentPage = new PageImpl<>(comments, pageable, 2);

        CommentDto dto1 = CommentDto.builder()
                .id(COMMENT_ID)
                .content("Comment 1")
                .authorId(USER_ID)
                .build();

        CommentDto dto2 = CommentDto.builder()
                .id(COMMENT_ID + 1)
                .content("Comment 2")
                .authorId(USER_ID + 1)
                .build();

        when(postRepository.existsById(POST_ID)).thenReturn(true);
        when(commentRepository.findByPostIdOrderByCreatedAtDesc(POST_ID, pageable)).thenReturn(commentPage);
        when(commentMapper.toDto(comment1)).thenReturn(dto1);
        when(commentMapper.toDto(comment2)).thenReturn(dto2);

        // Act
        Page<CommentDto> result = commentService.getCommentsByPostId(POST_ID, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getId()).isEqualTo(COMMENT_ID);
        assertThat(result.getContent().get(1).getId()).isEqualTo(COMMENT_ID + 1);

        verify(postRepository, times(1)).existsById(POST_ID);
        verify(commentRepository, times(1)).findByPostIdOrderByCreatedAtDesc(POST_ID, pageable);
        verify(commentMapper, times(2)).toDto(any(Comment.class));
    }

    @Test
    @DisplayName("Should return empty page when post has no comments")
    void getCommentsByPostId_WithNoComments_ShouldReturnEmptyPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(postRepository.existsById(POST_ID)).thenReturn(true);
        when(commentRepository.findByPostIdOrderByCreatedAtDesc(POST_ID, pageable)).thenReturn(emptyPage);

        // Act
        Page<CommentDto> result = commentService.getCommentsByPostId(POST_ID, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();

        verify(postRepository, times(1)).existsById(POST_ID);
        verify(commentRepository, times(1)).findByPostIdOrderByCreatedAtDesc(POST_ID, pageable);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when post does not exist")
    void getCommentsByPostId_WithNonExistentPost_ShouldThrowException() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        when(postRepository.existsById(POST_ID)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> commentService.getCommentsByPostId(POST_ID, pageable))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Post with id " + POST_ID + " not found");

        verify(postRepository, times(1)).existsById(POST_ID);
        verify(commentRepository, never()).findByPostIdOrderByCreatedAtDesc(anyLong(), any(Pageable.class));
    }
}
