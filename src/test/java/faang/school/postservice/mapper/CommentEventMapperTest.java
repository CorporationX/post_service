package faang.school.postservice.mapper;

import faang.school.postservice.dto.CommentEventDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CommentEventMapperTest {
    private final CommentEventMapper mapper = Mappers.getMapper(CommentEventMapper.class);
    private CommentEventDto dto;
    private Comment comment;

    @BeforeEach
    void setUp() {
        long postId = 1L;
        LocalDateTime dateTime = LocalDateTime.of(2023, 2, 2, 0, 0, 0);
        Post post = Post.builder().id(postId).build();

        dto = CommentEventDto.builder()
                .postId(postId)
                .authorId(2L)
                .commentId(3L)
                .date(dateTime)
                .build();

        comment = Comment.builder()
                .post(post)
                .authorId(2L)
                .id(3L)
                .createdAt(dateTime)
                .build();
    }

    @Test
    void testToCommentEntity() {
        Comment comment = mapper.toCommentEntity(dto, 1L);

        assertEquals(1L, comment.getPost().getId());
        assertEquals(dto.getAuthorId(), comment.getAuthorId());
        assertEquals(dto.getCommentId(), comment.getId());
        assertEquals(dto.getDate(), comment.getCreatedAt());

    }

    @Test
    void testToCommentDto() {
        CommentEventDto dto = mapper.toCommentDto(comment);

        assertEquals(comment.getId(), dto.getCommentId());
        assertEquals(comment.getAuthorId(), dto.getAuthorId());
        assertEquals(comment.getPost().getId(), dto.getPostId());
        assertEquals(comment.getCreatedAt(), dto.getDate());
    }

    @Test
    void testMapPostIdToPost() {
        Long postId = 1L;

        Post post = mapper.mapPostIdToPost(postId);

        assertEquals(postId, post.getId());
    }
}