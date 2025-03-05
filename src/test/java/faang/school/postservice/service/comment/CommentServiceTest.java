package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.kafka.producer.KafkaCommentProducer;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.comment.CommentValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    CommentValidator commentValidator;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private CommentService commentService;

    @Mock
    private KafkaCommentProducer kafkaCommentProducer;

    @Test
    @DisplayName("Positive test addComment")
    void testPositiveAddComment() {
        Long userId = 1L;
        Long postId = 1L;

        CommentDto commentDto = CommentTestData.getCommentDto(1L, "Nice", userId, postId, Collections.emptyList());
        Comment expectedComment = CommentTestData.getComment(1L, "Nice", userId, postId, Collections.emptyList());
        ResponseCommentDto expectedResponseDto = CommentTestData.getResponseCommentDto(1L, "Nice", userId, postId, Collections.emptyList());

        when(userServiceClient.getUser(userId)).thenReturn(new UserDto(userId, "name", "email"));
        when(userContext.getUserId()).thenReturn(userId);
        when(postRepository.findById(postId)).thenReturn(Optional.of(CommentTestData.getPost(postId, userId)));
        when(commentMapper.toEntity(commentDto)).thenReturn(expectedComment);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Возвращает переданный объект

        ResponseCommentDto actualResponseDto = commentService.addComment(postId, commentDto);

        assertNotNull(actualResponseDto, "Response DTO should not be null");
        assertEquals(expectedResponseDto, actualResponseDto, "Response DTO should match expected");
        assertEquals(Collections.emptyList(), actualResponseDto.getLikeIds(), "Like IDs should be empty");

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());
        Comment savedComment = captor.getValue();

        assertEquals(userId, savedComment.getAuthorId(), "Saved comment author ID should match userContext.getUserId()");
        assertEquals(postId, savedComment.getPost().getId(), "Saved comment post ID should match provided post ID");
    }

    @Test
    @DisplayName("Negative test addComment - User not found")
    void testNegativeAddComment_UserNotFound() {
        // Arrange
        Long userId = 999L;
        Long postId = 1L;
        CommentDto commentDto = CommentTestData.getCommentDto(1L, "Nice", userId, postId, Collections.emptyList());

        when(userServiceClient.getUser(userId)).thenThrow(new UserNotFoundException("User not found"));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            commentService.addComment(postId, commentDto);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Test updateComment")
    void testUpdateComment() {
        Long userId = 1L;
        CommentDto actualReceivedDto = CommentTestData.getCommentDto(1L, "Nice", userId, 1L, Arrays.asList(1L));
        Comment actualComment = CommentTestData.getComment(1L, "Nice", userId, 1L, Arrays.asList(Like.builder().id(1L).build()));
        ResponseCommentDto actualResponseDto = CommentTestData.getResponseCommentDto(1L, "Nice", userId, 1L, Arrays.asList(1L));

        when(commentRepository.findById(actualReceivedDto.getPostId())).thenReturn(Optional.of(actualComment));
        when(commentRepository.save(actualComment)).thenReturn(actualComment);
        when(commentMapper.toResponseDto(actualComment)).thenReturn(actualResponseDto);

        ResponseCommentDto expectedResponseDto = commentService.updateComment(actualReceivedDto);
        assertEquals(expectedResponseDto, actualResponseDto);
        assertEquals(actualResponseDto.getId(), expectedResponseDto.getId());
    }

    @Test
    @DisplayName("Test getCommentByPostId")
    void testGetCommentsByPostId() {
        Comment actualComment = CommentTestData.getComment(1L, "comment", 1L, 1L, List.of(Like.builder().id(1L).build()));
        List<Comment> comments = List.of(actualComment);
        List<Long> actualCommentIds = List.of(1L);
        Post post = Post.builder().id(1L).comments(comments).build();
        ResponseCommentDto actualResponseDto = CommentTestData.getResponseCommentDto(1L, "comment", 1L, post.getId(), actualCommentIds);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentMapper.toResponseDto(actualComment)).thenReturn(actualResponseDto);

        List<ResponseCommentDto> expectedListResponseDto = commentService.getCommentsByPostId(1L);
        assertEquals(expectedListResponseDto.size(), post.getComments().size());
        assertEquals(expectedListResponseDto.get(0), actualResponseDto);
    }

    @Test
    @DisplayName("Test method deleteComment at throw exception")
    void testDeleteComment() {
        Long id = 1L;
        when(commentRepository.existsById(id)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> commentService.deleteComment(id));
    }
}