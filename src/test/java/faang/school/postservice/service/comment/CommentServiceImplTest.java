package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static faang.school.postservice.service.comment.CommentServiceImplData.buildCommentEntity;
import static faang.school.postservice.service.comment.CommentServiceImplData.buildCommentViewDto;
import static faang.school.postservice.service.post.PostServiceTestData.buildPostEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для сервиса комментариев")
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper mapper;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private UserContext userContext;

    @Mock
    private CommentEventPublisherService eventPublisherService;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    @DisplayName("Успешное создание комментария")
    void create_success() {
        var currentUserId = 1L;
        var currentPostId = 2L;
        var currentCommentId = 3L;

        var post = buildPostEntity(
                currentPostId,
                currentUserId,
                null,
                LocalDateTime.now()
        );

        var commentCreateDto = new CommentCreateDto(
                "Привет я комментарий",
                currentUserId
        );

        var comment = buildCommentEntity(
                currentUserId,
                post,
                "Привет я комментарий"
        );

        var savedComment = buildCommentEntity(
                currentCommentId,
                currentUserId,
                post,
                "Привет я комментарий",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        var commentViewDto = buildCommentViewDto(savedComment);

        when(userContext.getUserId()).thenReturn(currentUserId);

        when(postRepository.getRequiredById(currentPostId)).thenReturn(post);

        when(commentRepository.save(comment)).thenReturn(savedComment);

        when(mapper.toViewDto(savedComment)).thenReturn(commentViewDto);

        var actual = commentService.create(currentPostId, commentCreateDto);

        assertEquals(commentViewDto, actual);

        verify(eventPublisherService).publishAsync(savedComment, post);

        verify(userServiceClient).getUser(currentUserId);

    }

    @Test
    void delete() {
        var commentId = 1L;
        var currentUserId = 2L;

        var comment = buildCommentEntity(
                currentUserId,
                null,
                "Привет я комментарий"
        );
        comment.setId(commentId);

        when(userContext.getUserId()).thenReturn(currentUserId);
        when(commentRepository.getRequiredById(commentId)).thenReturn(comment);

        commentService.delete(commentId);

        verify(commentRepository).deleteById(commentId);
    }
}