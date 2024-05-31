package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.util.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CommentMapperTest {

    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    private Comment comment;

    private CommentDto commentDto;

    private List<CommentDto> commentDtos;

    private List<Comment> comments;
    @Spy
    private TestData testData;

    @BeforeEach
    void init() {
        comment = testData.returnComment();

        commentDto = new CommentDto();
        commentDto.setId(2L);
        commentDto.setAuthorId(1L);
        commentDto.setContent("NewContent");
        commentDto.setCreatedAt(LocalDateTime.of(2024, Month.MAY, 31, 0, 0, 0));

        commentDtos = new ArrayList<>();
        commentDtos.add(commentDto);
        comments = new ArrayList<>();
        comments.add(comment);
    }

    @Test
    void testToEntity() {
        Comment actualComment = commentMapper.ToEntity(commentDto);
        assertEquals(comment, actualComment);
    }

    @Test
    void testToDto() {
        CommentDto actualCommentDto = commentMapper.ToDto(comment);
        assertEquals(actualCommentDto, commentDto);
    }

    @Test
    void testToDtoList() {
        List<CommentDto> actualCommentDtos = commentMapper.ToDtoList(comments);
        assertEquals(actualCommentDtos, commentDtos);
    }
}
