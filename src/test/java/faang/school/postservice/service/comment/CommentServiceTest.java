package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.config.moderation.ModerationDictionary;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.comment.CommentValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
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

    @Test
    @DisplayName("Positive test addComment")
    void testPositiveAddComment() {
        Long userId = 1L;
        CommentDto actualDto = CommentTestData.getCommentDto(1L, "Nice", userId, 1L);
        Comment actualComment = CommentTestData.getComment(1L, "Nice", userId, 1L);
        ResponseCommentDto actualResponseDto = CommentTestData.getResponseCommentDto(1L, "Nice", userId, 1L, Arrays.asList(1L));

        when(userServiceClient.getUser(userId)).thenReturn(new UserDto(1L, "name", "email"));
        when(commentMapper.toEntity(actualDto)).thenReturn(actualComment);
        when(userContext.getUserId()).thenReturn(userId);
        when(postRepository.findById(1L)).thenReturn(Optional.of(Post.builder().id(1L).build()));
        when(commentRepository.save(actualComment)).thenReturn(actualComment);
        when(commentMapper.toResponseDto(actualComment)).thenReturn(actualResponseDto);

        ResponseCommentDto inspectedResponseDto = commentService.addComment(1L, actualDto);

        assertEquals(inspectedResponseDto, actualResponseDto);
        assertEquals(actualResponseDto.getId(), inspectedResponseDto.getId());
    }

    @Test
    @DisplayName("Negative test addComment")
    void testNegativeAddComment() {
        Long userId = 1L;
        CommentDto actualDto = CommentTestData.getCommentDto(1L, "Nice", userId, 1L);
        Comment actualComment = CommentTestData.getComment(1L, "Nice", userId, 1L);
        ResponseCommentDto actualResponseDto = CommentTestData.getResponseCommentDto(1L, "Nice", userId, 1L, List.of(1L));
        ResponseCommentDto returnedResponseDto = CommentTestData.getResponseCommentDto(2L, "Nice", userId, 1L, List.of(1L));

        when(userServiceClient.getUser(userId)).thenReturn(new UserDto(1L, "name", "email"));
        when(commentMapper.toEntity(actualDto)).thenReturn(actualComment);
        when(userContext.getUserId()).thenReturn(userId);
        when(postRepository.findById(1L)).thenReturn(Optional.of(Post.builder().id(2L).build()));
        when(commentRepository.save(actualComment)).thenReturn(actualComment);
        when(commentMapper.toResponseDto(actualComment)).thenReturn(returnedResponseDto);

        ResponseCommentDto inspectedResponseDto = commentService.addComment(1L, actualDto);

        assertNotEquals(inspectedResponseDto, actualResponseDto);
        assertNotEquals(actualResponseDto.getId(), inspectedResponseDto.getId());
    }

    @Test
    @DisplayName("Test updateComment")
    void testUpdateComment() {
        Long userId = 1L;
        CommentDto actualReceivedDto = CommentTestData.getCommentDto(1L, "Nice", userId, 1L);
        Comment actualComment = CommentTestData.getComment(1L, "Nice", userId, 1L);
        ResponseCommentDto actualResponseDto = CommentTestData.getResponseCommentDto(1L, "Nice", userId, 1L, Arrays.asList(1L));

        when(commentRepository.findById(actualReceivedDto.getPostId())).thenReturn(Optional.of(actualComment));
        when(commentRepository.save(actualComment)).thenReturn(actualComment);
        when(commentMapper.toResponseDto(actualComment)).thenReturn(actualResponseDto);

        ResponseCommentDto inspectedResponseDto = commentService.updateComment(actualReceivedDto);
        assertEquals(inspectedResponseDto, actualResponseDto);
        assertEquals(actualResponseDto.getId(), inspectedResponseDto.getId());
    }

    @Test
    @DisplayName("Test getCommentByPostId")
    void testGetCommentsByPostId() {
        Comment actualComment = CommentTestData.getComment(1L, "comment",1L, 1L);
        List<Comment> comments = List.of(actualComment);
        List<Long> actualCommentIds = List.of(1L);
        Post post = Post.builder().id(1L).comments(comments).build();
        ResponseCommentDto actualResponseDto = CommentTestData.getResponseCommentDto(1L, "comment", 1L, post.getId(), actualCommentIds);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentMapper.toResponseDto(actualComment)).thenReturn(actualResponseDto);

        List<ResponseCommentDto> inspectedListResponseDto = commentService.getCommentsByPostId(1L);
        assertEquals(inspectedListResponseDto.size(), post.getComments().size());
        assertEquals(inspectedListResponseDto.get(0), actualResponseDto);
    }

    @Test
    @DisplayName("Test method deleteComment at throw exception")
    void testDeleteComment() {
        Long id = 1L;
        when(commentRepository.existsById(id)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> commentService.deleteComment(id));
    }
}
