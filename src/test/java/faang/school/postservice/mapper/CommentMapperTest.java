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
        Long postId = 1L;
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .content("Test comment")
                .authorId(2L)
                .postId(postId)
                .build();

        Comment expectedComment = Comment.builder()
                .id(1L)
                .content("Test comment")
                .authorId(2L)
                .build();
        Post post = Post.builder().id(postId).build();
        expectedComment.setPost(post);

        Comment result = commentMapper.toEntity(commentDto, postId);

        assertEquals(expectedComment.getId(), result.getId());
        assertEquals(expectedComment.getContent(), result.getContent());
        assertEquals(expectedComment.getAuthorId(), result.getAuthorId());
        assertEquals(expectedComment.getPost().getId(), result.getPost().getId());
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
//
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