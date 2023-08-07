package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static junit.framework.TestCase.assertEquals;

@ExtendWith(MockitoExtension.class)
class CommentMapperTest {
    private static final long COMMENT_ID = 1L;
    private static final long AUTHOR_ID = 3L;
    private static final Post POST = Post.builder().id(2L).content("Post...").build();
    private static final Post POSTfromDto = Post.builder().id(2L).build();
    private CommentMapper commentMapper = new CommentMapperImpl();
    private Comment comment;
    private Comment commentFromDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        comment = Comment.builder().id(COMMENT_ID).authorId(AUTHOR_ID).content("content").post(POST).likes(null).build();
        commentFromDto = Comment.builder().id(COMMENT_ID).authorId(AUTHOR_ID).content("content").post(POSTfromDto).likes(null).build();
        commentDto = CommentDto.builder().id(COMMENT_ID).authorId(AUTHOR_ID).content("content").postId(POST.getId()).build();
    }

    @Test
    public void testToDtoMethodValid() {
        CommentDto commentDtoFromMapper = commentMapper.toDto(comment);
        assertEquals(commentDto, commentDtoFromMapper);
    }

    @Test
    public void testToEntityMethodValid() {
        Comment commentFromMapper = commentMapper.toEntity(commentDto);
        assertEquals(commentFromDto, commentFromMapper);
    }

    @Test
    public void testUpdateMethodValid() {
        Comment commentToUpdate = Comment.builder().id(11L).authorId(11L).post(new Post()).content("Other...").build();
        commentMapper.update(commentDto, commentToUpdate);
        assertEquals(commentFromDto, commentToUpdate);
    }
}