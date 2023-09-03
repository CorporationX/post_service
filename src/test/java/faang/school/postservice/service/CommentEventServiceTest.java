package faang.school.postservice.service;

import faang.school.postservice.dto.CommentEventDto;
import faang.school.postservice.mapper.CommentEventMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentEventValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentEventServiceTest {
    @InjectMocks
    private CommentEventService service;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentEventValidator validator;
    @Mock
    private CommentEventMapper mapper;
    @Mock
    private CommentEventPublisher publisher;

    @Test
    void testCreateCommentEvent_Success() {
        long postId = 1L;
        LocalDateTime dateTime = LocalDateTime.now().withNano(0);
        CommentEventDto dto = CommentEventDto.builder()
                .postId(postId)
                .authorId(2L)
                .commentId(3L)
                .date(dateTime)
                .build();

        Comment expectedComment = new Comment();

        when(mapper.toCommentEntity(dto, postId)).thenReturn(expectedComment);
        when(commentRepository.save(expectedComment)).thenReturn(expectedComment);
        when(mapper.toCommentDto(expectedComment)).thenReturn(dto);

        CommentEventDto result = service.createCommentEvent(postId, dto);

        assertNotNull(result);
        assertEquals(dto.getPostId(), result.getPostId());
        assertEquals(dto.getAuthorId(), result.getAuthorId());
        assertEquals(dto.getCommentId(), result.getCommentId());
        assertEquals(dto.getDate(), result.getDate());

        verify(validator).validateBeforeCreate(dto);
        verify(mapper, times(1)).toCommentEntity(dto, postId);
        verify(commentRepository, times(1)).save(expectedComment);
        verify(publisher, times(1)).publish(dto);
    }

    @Test
    void testCreateCommentEvent_ValidationFails() {
        long postId = 1L;
        LocalDateTime dateTime = LocalDateTime.now().withSecond(0).withNano(0);
        CommentEventDto dto = CommentEventDto.builder()
                .postId(postId)
                .authorId(2L)
                .commentId(3L)
                .date(dateTime)
                .build();

        doThrow(ValidationException.class).when(validator).validateBeforeCreate(dto);

        assertThrows(ValidationException.class, () -> service.createCommentEvent(postId, dto));

        verify(validator).validateBeforeCreate(dto);
        verifyNoInteractions(mapper, commentRepository, publisher);
    }

    @Test
    void testCreateCommentEvent_InvalidInputData() {
        long postId = 1L;
        LocalDateTime dateTime = LocalDateTime.now().withSecond(0).withNano(0);
        CommentEventDto dto = CommentEventDto.builder()
                .postId(postId)
                .authorId(2L)
                .commentId(3L)
                .date(dateTime)
                .build();

        doThrow(EntityNotFoundException.class).when(validator).validateBeforeCreate(dto);

        assertThrows(EntityNotFoundException.class, () -> service.createCommentEvent(postId, dto));

        verify(validator).validateBeforeCreate(dto);
        verifyNoInteractions(mapper, commentRepository, publisher);
    }
}