package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CommentMapperTest {

    @InjectMocks
    private CommentMapper mapper = Mappers.getMapper(CommentMapper.class);

    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        Post post = new Post();
        post.setId(1L);
        comment = Comment.builder()
                .id(1L)
                .content("Text")
                .authorId(2L)
                .post(post)
                .build();
        commentDto = CommentDto.builder()
                .id(1L)
                .content("Text")
                .authorId(2L)
                .postId(3L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testToDtoWorksCorrectly() {
        CommentDto resultDto = mapper.toDto(comment);
        assertCommentDto(resultDto);
    }

    private void assertCommentDto(CommentDto commentDto) {
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getContent(), commentDto.getContent());
        assertEquals(comment.getAuthorId(), commentDto.getAuthorId());
        assertEquals(comment.getPost().getId(), commentDto.getPostId());
        assertEquals(comment.getCreatedAt(), commentDto.getCreatedAt());
    }

    private void assertComment(Comment comment) {
        assertEquals(commentDto.getId(), comment.getId());
        assertEquals(commentDto.getContent(), comment.getContent());
        assertEquals(commentDto.getAuthorId(), comment.getAuthorId());
        assertEquals(commentDto.getCreatedAt(), comment.getCreatedAt());
        assertNull(comment.getLikes());
        assertNull(comment.getPost());
    }
}
