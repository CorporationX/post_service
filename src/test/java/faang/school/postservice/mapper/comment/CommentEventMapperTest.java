package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentEventDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.test_data.TestDataComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class CommentEventMapperTest {
    @InjectMocks
    private CommentEventMapper commentEventMapper;
    private Post post;
    private Comment comment;
    private CommentEventDto newCommentEventDto;
    private CommentEventDto expectedCommentEventDto;

    @BeforeEach
    void setUp() {
        TestDataComment testDataComment = new TestDataComment();
        post = testDataComment.getPost();
        comment = testDataComment.getComment1();
        newCommentEventDto = new CommentEventDto();
        expectedCommentEventDto = testDataComment.getCommentEventDto();
    }

    @Test
    void testToEvent_Success() {
        newCommentEventDto = commentEventMapper.toEvent(comment, post);

        assertNotNull(newCommentEventDto);
        assertThat(newCommentEventDto).usingRecursiveComparison()
                .ignoringActualNullFields()
                .isEqualTo(expectedCommentEventDto);
    }
}