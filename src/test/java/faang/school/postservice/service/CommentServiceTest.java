package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);
    @InjectMocks
    private CommentService commentService;

    @Test
    void createCommentTest(){
        CommentDto commentDto = CommentDto.builder().content("content").build();
        Comment comment = Comment.builder().content("content").build();

        CommentDto expectedDto = CommentDto.builder().id(0L).content("content").authorId(0L).build();
        CommentDto result = commentService.create(commentDto);

        Mockito.verify(commentRepository).save(comment);
        assertEquals(expectedDto, result);
    }
}