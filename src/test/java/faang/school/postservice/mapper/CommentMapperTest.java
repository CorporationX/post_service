package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentRequest;
import faang.school.postservice.dto.comment.UpdateCommentRequest;
import faang.school.postservice.model.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class CommentMapperTest {
    private final long id = 1L;
    private final long authorId = 1L;
    private final String content = "test";

    @InjectMocks
    private CommentMapperImpl commentMapper;

    @Test
    @DisplayName("success mapping CreateCommentRequest in Comment")
    void testCreateCommentRequestToComment() {

        CreateCommentRequest createCommentRequest = CreateCommentRequest.builder()
                .authorId(authorId)
                .content(content)
                .build();

        Comment comment = commentMapper.toComment(createCommentRequest);

        System.out.println(comment);

        assertEquals(comment.getAuthorId(), createCommentRequest.getAuthorId());
        assertEquals(comment.getContent(), createCommentRequest.getContent());
        assertEquals(comment.getId(), 0);
        assertNull(comment.getPost());
        assertNull(comment.getCreatedAt());
        assertNull(comment.getUpdatedAt());

    }

    @Test
    @DisplayName("success mapping UpdateCommentRequest in Comment")
    void testUpdateCommentRequestToComment() {

        UpdateCommentRequest updateCommentRequest = UpdateCommentRequest.builder()
                .authorId(authorId)
                .content(content)
                .build();

        Comment comment = commentMapper.toComment(updateCommentRequest);

        assertEquals(comment.getAuthorId(), updateCommentRequest.getAuthorId());
        assertEquals(comment.getContent(), updateCommentRequest.getContent());
        assertEquals(comment.getId(), 0);
        assertNull(comment.getPost());
        assertNull(comment.getCreatedAt());
        assertNull(comment.getUpdatedAt());
    }

    @Test
    @DisplayName("success mapping Comment to CommentDto")
    void testToCommentDto() {
        Comment comment = Comment.builder()
                .id(id)
                .authorId(authorId)
                .content(content)
                .createdAt(LocalDateTime.of(2024, 9, 23, 19, 4))
                .updatedAt(LocalDateTime.of(2024, 9, 25, 11, 32))
                .build();

        CommentDto commentDto = commentMapper.toCommentDto(comment);

        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getAuthorId(), commentDto.getAuthorId());
        assertEquals(comment.getContent(), commentDto.getContent());
        assertEquals(comment.getCreatedAt(), commentDto.getCreatedAt());
        assertEquals(comment.getUpdatedAt(), commentDto.getUpdatedAt());
    }
}