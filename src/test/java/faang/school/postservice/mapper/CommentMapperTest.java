package faang.school.postservice.mapper;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void testToEntity() {

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .content("Test comment")
                .authorId(1L)
                .postId(100L)
                .createdAt(null)
                .build();

        Comment comment = commentMapper.toEntity(commentDto);

        assertEquals(commentDto.getId(), comment.getId());
        assertEquals(commentDto.getContent(), comment.getContent());
        assertEquals(commentDto.getAuthorId(), comment.getAuthorId());
        assertEquals(commentDto.getPostId(), comment.getPost().getId());
        assertEquals(commentDto.getCreatedAt(), comment.getCreatedAt());
    }

    @Test
    void testToDto() {

        Comment comment = Comment.builder()
                .id(1L)
                .content("Test comment")
                .authorId(1L)
                .post(Post.builder().id(100L).build())
                .createdAt(null)
                .build();

        CommentDto commentDto = commentMapper.toDto(comment);

        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getContent(), commentDto.getContent());
        assertEquals(comment.getAuthorId(), commentDto.getAuthorId());
        assertEquals(comment.getPost().getId(), commentDto.getPostId());
        assertEquals(comment.getCreatedAt(), commentDto.getCreatedAt());
    }

    @Test
    void testPartialUpdate() {

        Comment existingComment = Comment.builder()
                .id(1L)
                .content("Existing comment content")
                .authorId(1L)
                .post(Post.builder().id(100L).build())
                .createdAt(null)
                .build();

        CommentDto commentDto = CommentDto.builder()
                .content("Updated comment content")
                .authorId(2L)
                .postId(100L)
                .build();


        Comment updatedComment = commentMapper.partialUpdate(commentDto, existingComment);

        assertEquals(existingComment.getId(), updatedComment.getId());
        assertEquals(commentDto.getContent(), updatedComment.getContent());
        assertEquals(commentDto.getAuthorId(), updatedComment.getAuthorId());
        assertEquals(commentDto.getPostId(), updatedComment.getPost().getId());
        assertEquals(existingComment.getCreatedAt(), updatedComment.getCreatedAt());
    }
}