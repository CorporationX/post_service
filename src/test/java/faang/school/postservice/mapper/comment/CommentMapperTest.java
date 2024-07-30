package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommentMapperTest {

    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);
    private Comment comment;
    private CommentDto commentDto;
    private Comment expectedComment;
    private CommentDto expectedCommentDto;

    @BeforeEach
    public void setUp() {
        Like like = new Like();
        like.setId(1);
        Post post = new Post();
        post.setId(1L);

        LocalDateTime createdAt = LocalDateTime.of(2024, 7, 21, 0, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 7, 21, 0, 0);

        comment = Comment.builder()
                .id(1)
                .content("content")
                .authorId(1L)
                .likes(List.of(like))
                .post(post)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .content("content")
                .authorId(1L)
                .likeIds(List.of(1L))
                .postId(1L)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        expectedComment = Comment.builder()
                .id(1)
                .content("content")
                .authorId(1L)
                .likes(List.of(Like.builder().id(1).build()))
                .post(Post.builder().id(1L).build())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        expectedCommentDto = CommentDto.builder()
                .id(1L)
                .content("content")
                .authorId(1L)
                .likeIds(List.of(1L))
                .postId(1L)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    @Test
    @DisplayName("Проверка преобразования Entity to DTO")
    public void testCommentToCommentDto() {
        CommentDto resultCommentDto = commentMapper.toDto(comment);
        assertEquals(expectedCommentDto, resultCommentDto);
    }

    @Test
    @DisplayName("Проверка преобразования DTO to Entity")
    public void testCommentDtoToComment() {
        Comment resultComment = commentMapper.toEntity(commentDto);
        assertEquals(expectedComment, resultComment);
    }
}
