package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommentMapperTest {

    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Test
    @DisplayName("Проверка преобразования Entity to DTO")
    public void testCommentToCommentDto() {
        Like like = new Like();
        like.setId(1);
        Post post = new Post();
        post.setId(1L);

        LocalDateTime createdAt = LocalDateTime.of(2024, 7, 21, 0, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 7, 21, 0, 0);

        Comment comment = Comment.builder()
                .id(1)
                .content("content")
                .authorId(1L)
                .likes(List.of(like))
                .post(post)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        CommentDto commentDto = commentMapper.toDto(comment);

        assertNotNull(commentDto);
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getContent(), commentDto.getContent());
        assertEquals(comment.getAuthorId(), commentDto.getAuthorId());
        assertEquals(1, commentDto.getLikeIds().size());
        assertEquals(comment.getLikes().get(0).getId(), commentDto.getLikeIds().get(0));
        assertEquals(comment.getPost().getId(), commentDto.getPostId());
        assertEquals(comment.getCreatedAt(), commentDto.getCreatedAt());
        assertEquals(comment.getUpdatedAt(), commentDto.getUpdatedAt());
    }

    @Test
    @DisplayName("Проверка преобразования DTO to Entity")
    public void testCommentDtoToComment() {
        Long postId = 1L;
        Long authorId = 1L;
        LocalDateTime createdAt = LocalDateTime.of(2024, 7, 21, 0, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 7, 21, 0, 0);

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .content("content")
                .authorId(authorId)
                .likeIds(List.of(1L))
                .postId(postId)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        Comment comment = commentMapper.toEntity(commentDto);

        assertNotNull(comment);
        assertEquals(commentDto.getId(), comment.getId());
        assertEquals(commentDto.getContent(), comment.getContent());
        assertEquals(commentDto.getAuthorId(), comment.getAuthorId());
        assertEquals(1, comment.getLikes().size());
        assertEquals(commentDto.getLikeIds().get(0), comment.getLikes().get(0).getId());
        assertEquals(commentDto.getPostId(), comment.getPost().getId());
        assertEquals(commentDto.getCreatedAt(), comment.getCreatedAt());
        assertEquals(commentDto.getUpdatedAt(), comment.getUpdatedAt());
    }
}