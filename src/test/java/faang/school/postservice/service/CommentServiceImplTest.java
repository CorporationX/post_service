package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.dto.CommentCount;
import faang.school.postservice.model.dto.CommentDto;
import faang.school.postservice.model.entity.Comment;
import faang.school.postservice.model.event.application.CommentCommittedEvent;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.impl.CommentServiceImpl;
import faang.school.postservice.validator.comment.CommentServiceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper mapper;

    @Mock
    private CommentServiceValidator validator;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCommentCount() {
        Long postId = 1L;

        when(commentRepository.countByPostId(postId)).thenReturn(5);

        int result = commentService.getCommentCount(postId);

        assertEquals(5, result);
        verify(commentRepository).countByPostId(postId);
    }

    @Test
    void testGetRecentComments() {
        Long postId = 1L;
        int numberOfComments = 3;

        List<Comment> comments = List.of(new Comment());
        when(commentRepository.findRecentByPostId(eq(postId), any(Pageable.class))).thenReturn(comments);
        when(mapper.mapToCommentDto(comments)).thenReturn(List.of(new CommentDto()));

        List<CommentDto> result = commentService.getRecentComments(postId, numberOfComments);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(commentRepository).findRecentByPostId(eq(postId), any(Pageable.class));
    }

    @Test
    void testGetTop3CommentsForPosts() {
        List<Long> postIds = List.of(1L, 2L);
        Object[] row1 = {1L, 1L, "Content 1", 10L, Instant.now()};
        Object[] row2 = {2L, 2L, "Content 2", 20L, Instant.now()};

        when(commentRepository.findTop3CommentsPerPost(postIds)).thenReturn(List.of(row1, row2));

        Map<Long, List<CommentDto>> result = commentService.getTop3CommentsForPosts(postIds);

        assertNotNull(result);
        assertTrue(result.containsKey(1L));
        assertTrue(result.containsKey(2L));
    }

    @Test
    void testGetPostIdCommentCountMap() {
        List<Long> postIds = List.of(1L, 2L);

        CommentCount count1 = new CommentCount(1L, 5L);
        when(commentRepository.findCommentCountsByPostIds(postIds)).thenReturn(List.of(count1));

        Map<Long, Integer> result = commentService.getPostIdCommentCountMap(postIds);

        assertNotNull(result);
        assertEquals(5, result.get(1L));
        assertEquals(0, result.get(2L));
    }

    @Test
    void testCreateComment() {
        // Arrange
        Long userId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setPostId(10L);
        commentDto.setContent("Test content");

        Comment comment = new Comment();
        Comment savedComment = new Comment();
        CommentDto savedCommentDto = new CommentDto();
        savedCommentDto.setId(100L);

        when(mapper.mapToComment(commentDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(savedComment);
        when(mapper.mapToCommentDto(savedComment)).thenReturn(savedCommentDto);

        // Act
        CommentDto result = commentService.createComment(commentDto, userId);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
        verify(validator).validatePostExist(10L);
        verify(validator).validateCommentContent("Test content");
        verify(applicationEventPublisher).publishEvent(any(CommentCommittedEvent.class));
    }

    @Test
    void testGetComment() {
        // Arrange
        Long postId = 1L;

        Comment comment1 = new Comment();
        comment1.setUpdatedAt(LocalDateTime.now().minusMinutes(1));

        Comment comment2 = new Comment();
        comment2.setUpdatedAt(LocalDateTime.now());

        List<Comment> comments = List.of(comment1, comment2);
        when(commentRepository.findAllByPostId(postId)).thenReturn(comments);

        CommentDto dto1 = new CommentDto();
        CommentDto dto2 = new CommentDto();
        when(mapper.mapToCommentDto(anyList())).thenReturn(List.of(dto2, dto1));

        // Act
        List<CommentDto> result = commentService.getComment(postId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(commentRepository).findAllByPostId(postId);
    }

    @Test
    void testDeleteComment() {
        // Arrange
        Long commentId = 1L;

        // Act
        commentService.deleteComment(commentId);

        // Assert
        verify(validator).validateCommentExist(commentId);
        verify(commentRepository).deleteById(commentId);
    }

    @Test
    void testUpdateComment() {
        // Arrange
        Long commentId = 1L;
        Long userId = 2L;
        CommentDto commentDto = new CommentDto();
        commentDto.setContent("Updated content");

        Comment comment = new Comment();
        Comment updatedComment = new Comment();
        CommentDto updatedDto = new CommentDto();
        updatedDto.setContent("Updated content");

        when(commentRepository.findById(commentId)).thenReturn(java.util.Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(updatedComment);
        when(mapper.mapToCommentDto(updatedComment)).thenReturn(updatedDto);

        // Act
        CommentDto result = commentService.updateComment(commentId, commentDto, userId);

        // Assert
        assertNotNull(result);
        assertEquals("Updated content", result.getContent());
        verify(validator).validateCommentExist(commentId);
        verify(validator).validateCommentContent("Updated content");
        verify(userServiceClient).getUser(userId);
        verify(commentRepository).save(comment);
    }

    @Test
    void testGetTop3CommentsAuthorIds() {
        // Arrange
        List<Long> postIds = List.of(1L, 2L);
        Object[] row1 = {1L, 10L};
        Object[] row2 = {2L, 20L};

        when(commentRepository.findTop3CommentsAuthorIdsPerPost(postIds)).thenReturn(List.of(row1, row2));

        // Act
        Map<Long, List<Long>> result = commentService.getTop3CommentsAuthorIds(postIds);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.get(1L).size());
        assertEquals(10L, result.get(1L).get(0));
        assertEquals(20L, result.get(2L).get(0));
    }

    @Test
    void testGetComment_NotFound() {
        // Arrange
        Long postId = 1L;
        when(commentRepository.findAllByPostId(postId)).thenReturn(List.of());

        // Act
        List<CommentDto> result = commentService.getComment(postId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}
