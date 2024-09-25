package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CommentMapperTest {

    @InjectMocks
    private CommentMapperImpl commentMapper;

    @Test
    @DisplayName("success mapping CommentDto in Comment")
    void testToComment() {

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .authorId(1L)
                .content("test")
                .createdAt(LocalDateTime.of(2024, 9, 23, 19, 4))
                .updatedAt(LocalDateTime.of(2024, 9, 25, 11, 32))
                .build();

        Comment comment = commentMapper.toComment(commentDto);

        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getAuthorId(), commentDto.getAuthorId());
        assertEquals(comment.getContent(), commentDto.getContent());
        assertEquals(comment.getCreatedAt(), commentDto.getCreatedAt());
        assertEquals(comment.getUpdatedAt(), commentDto.getUpdatedAt());
    }

    @Test
    @DisplayName("success mapping Comment to CommentDto")
    void testToCommentDto() {
        Comment comment = Comment.builder()
                .id(1L)
                .authorId(1L)
                .content("test")
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