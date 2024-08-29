package faang.school.postservice.mapper.comment;

import faang.school.postservice.event.comment.CommentEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {

    private CommentMapperImpl commentMapper = new CommentMapperImpl();

    private Comment comment;
    private CommentEvent commentEvent;

    @BeforeEach
    void setUp() {
        comment = Comment.builder()
                .id(1)
                .authorId(1)
                .post(Post.builder().id(1L).authorId(1L).build())
                .content("Test")
                .build();

        commentEvent = CommentEvent.builder()
                .commentId(1)
                .authorId(1)
                .postId(1)
                .postAuthorId(1)
                .content("Test")
                .build();
    }

    @Test
    void testToEvent() {
        CommentEvent result = commentMapper.toEvent(comment);

        assertEquals(commentEvent.getCommentId(), result.getCommentId());
        assertEquals(commentEvent.getAuthorId(), result.getAuthorId());
        assertEquals(commentEvent.getPostId(), result.getPostId());
        assertEquals(commentEvent.getPostAuthorId(), result.getPostAuthorId());
        assertEquals(commentEvent.getContent(), result.getContent());
    }
}