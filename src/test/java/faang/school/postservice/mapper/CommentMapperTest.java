package faang.school.postservice.mapper;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {

    private CommentMapper commentMapper = new CommentMapperImpl();

    private CommentDto dto;
    private Comment comment;

    private final LocalDateTime createdAt = LocalDateTime.now();
    private final LocalDateTime updatedAt = LocalDateTime.now().plusDays(1);

    @BeforeEach
    void setUp() {
        comment = Comment.builder()
                .id(1L)
                .content("Content")
                .authorId(2L)
                .post(Post.builder().id(2L).build())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
        dto = CommentDto.builder()
                .id(1L)
                .content("Content")
                .authorId(2L)
                .postId(2L)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    @Test
    void toDtoTest() {
        CommentDto result = commentMapper.toDto(comment);
        assertEquals(dto, result);
    }

    @Test
    void toEntityTest() {
        Comment expected = Comment.builder()
                .id(1L)
                .authorId(2L)
                .content("Content")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
        Comment result = commentMapper.toEntity(dto);

        assertEquals(expected, result);
    }
}