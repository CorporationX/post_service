package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validation.CommentValidation;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    CommentValidation commentValidation;
    @InjectMocks
    private CommentService commentService;
    CommentDto firstCommentDto;
    CommentDto secondCommentDto;

    Comment firstComment;
    Comment secondComment;


    @BeforeEach
    void setUp() {
        firstCommentDto = CommentDto.builder()
                .id(null)
                .content("first content")
                .authorId(1L)
                .likesIds(null)
                .build();
        firstComment = Comment.builder()
                .id(1L)
                .content("first content")
                .authorId(1L)
                .likes(null)
                .build();
        secondCommentDto = CommentDto.builder()
                .id(2L)
                .content("second content")
                .authorId(1L)
                .likesIds(null)
                .build();
        secondComment = Comment.builder()
                .id(2L)
                .content("edited second content")
                .authorId(1L)
                .likes(null)
                .build();

    }

    @Test
    public void testCreation() {
        Mockito.when(commentMapper.toEntity(firstCommentDto)).thenReturn(firstComment);
        Mockito.when(commentRepository.save(firstComment)).thenReturn(firstComment);
        Mockito.when(commentMapper.toDto(firstComment)).thenReturn(firstCommentDto);

        CommentDto result = commentService.create(firstCommentDto, firstCommentDto.getAuthorId());

        Mockito.verify(commentValidation, Mockito.times(1)).authorValidation(firstCommentDto.getAuthorId());

        Assert.assertEquals(result, firstCommentDto);
    }

    @Test
    public void testUpdating_NoCommentFound() {
        Mockito.when(commentRepository.existsById(firstCommentDto.getId())).thenReturn(false);
        DataValidationException e = Assert.assertThrows(DataValidationException.class, () -> commentService.update(firstCommentDto, firstCommentDto.getAuthorId()));

        Mockito.verify(commentValidation, Mockito.times(1)).authorValidation(firstCommentDto.getAuthorId());

        Assert.assertEquals(e.getMessage(), "No comment with this id found");
    }

    @Test
    public void testUpdating() {
        Mockito.when(commentRepository.existsById(secondCommentDto.getId())).thenReturn(true);
        Mockito.when(commentMapper.toEntity(secondCommentDto)).thenReturn(secondComment);
        Mockito.when(commentRepository.save(secondComment)).thenReturn(secondComment);
        Mockito.when(commentMapper.toDto(secondComment)).thenReturn(secondCommentDto);

        CommentDto result = commentService.update(secondCommentDto, secondCommentDto.getAuthorId());

        Mockito.verify(commentValidation, Mockito.times(1)).authorValidation(secondCommentDto.getAuthorId());
        Assert.assertEquals(result, secondCommentDto);
    }

    @Test
    public void testDeletion_authorNotFound() {
        Mockito.when(commentRepository.existsById(secondCommentDto.getId())).thenReturn(false);
        DataValidationException e = Assert.assertThrows(DataValidationException.class, () -> commentService.update(secondCommentDto, secondCommentDto.getAuthorId()));

        Mockito.verify(commentValidation, Mockito.times(1)).authorValidation(secondCommentDto.getAuthorId());

        Assert.assertEquals(e.getMessage(), "No comment with this id found");
    }

    @Test
    public void testDeletion() {
        Mockito.when(commentRepository.existsById(secondCommentDto.getId())).thenReturn(true);

        commentService.delete(secondCommentDto, secondCommentDto.getAuthorId());

        Mockito.verify(commentValidation, Mockito.times(1)).authorValidation(secondCommentDto.getAuthorId());
        Mockito.verify(commentRepository, Mockito.times(1)).deleteById(secondComment.getId());
    }
}

