package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.UpdatedCommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publishers.redis.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    CommentRepository commentRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    CommentMapper commentMapper;

    @Mock
    PostService postService;

    @InjectMocks
    CommentService commentService;

    private final CreateCommentDto createCommentDto = new CreateCommentDto();
    private final UpdatedCommentDto updatedCommentDto = new UpdatedCommentDto();
    private final Comment comment = new Comment();
    private final Post post = new Post();

    CreateCommentDto prepareCreateCommentDto() {
        createCommentDto.setContent("Test content");
        createCommentDto.setAuthorId(1L);
        createCommentDto.setPostId(1L);
        return createCommentDto;
    }

    Comment prepareComment() {
        comment.setId(1L);
        comment.setContent("Test comment's content");
        comment.setAuthorId(1L);
        comment.setPost(preparePost());
        return comment;
    }

    UpdatedCommentDto prepareUpdateCommentDto() {
        updatedCommentDto.setId(1L);
        updatedCommentDto.setAuthorId(1L);
        updatedCommentDto.setContent("Test content updated");
        return updatedCommentDto;
    }

    Comment prepareUpdateComment() {
        comment.setId(1L);
        comment.setContent("Test content updated");
        comment.setAuthorId(1L);
        comment.setPost(preparePost());
        return comment;
    }

    Post preparePost() {
        post.setId(1L);
        post.setContent("Test post's content");
        post.setAuthorId(20L);
        return post;
    }

    List<Comment> prepareCommentList() {
        LocalDateTime createTime = LocalDateTime.of(2024, Month.JULY, 20, 12, 15);
        Comment firstComment = new Comment();
        firstComment.setId(1L);
        firstComment.setCreatedAt(createTime.minusMonths(1));
        Comment secondComment = new Comment();
        secondComment.setId(2L);
        secondComment.setCreatedAt(createTime);
        Comment thirdComment = new Comment();
        thirdComment.setId(3L);
        thirdComment.setCreatedAt(createTime.minusWeeks(1));
        return List.of(firstComment, secondComment, thirdComment);
    }

    List<CommentDto> prepareCommentDtoList() {
        LocalDateTime createTime = LocalDateTime.of(2024, Month.JULY, 20, 12, 15);
        CommentDto firstCommentDto = new CommentDto();
        firstCommentDto.setId(1L);
        firstCommentDto.setCreatedAt(createTime.minusMonths(1));
        CommentDto secondCommentDto = new CommentDto();
        secondCommentDto.setId(2L);
        secondCommentDto.setCreatedAt(createTime);
        CommentDto thirdCommentDto = new CommentDto();
        thirdCommentDto.setId(3L);
        thirdCommentDto.setCreatedAt(createTime.minusWeeks(1));
        return List.of(firstCommentDto, secondCommentDto, thirdCommentDto);
    }

//    @Test
//    public void testCreateCommentSuccessful() throws JsonProcessingException {
//        CreateCommentDto createCommentDto = prepareCreateCommentDto();
//        Comment comment = prepareComment();
//
//        when(postService.getById(anyLong())).thenReturn(post);
//        when(commentRepository.save(comment)).thenReturn(comment);
//        when(commentMapper.toEntity(createCommentDto, post)).thenReturn(comment);
//
//        commentService.createComment(createCommentDto);
//
//        verify(commentRepository, times(1)).save(comment);
//    }

    @Test
    void testGetById() {
        long id = 1L;
        Comment comment = Comment.builder()
                .id(id)
                .build();

        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));

        Comment result = commentService.getById(id);

        assertEquals(comment, result);
        verify(commentRepository, times(1)).findById(id);
    }

    @Test
    void testGetById_notExists_throws() {
        long id = 1L;

        when(commentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.getById(id));
        verify(commentRepository, times(1)).findById(id);
    }

    @Test
    public void testUpdateCommentIfCommentDoesNotFound() {
        UpdatedCommentDto updatedCommentDto = prepareUpdateCommentDto();
        when(commentRepository.findById(updatedCommentDto.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.updateComment(updatedCommentDto));
    }

    @Test
    public void testUpdateCommentIfAuthorIsNotConfirmed() {
        UpdatedCommentDto updatedCommentDto = prepareUpdateCommentDto();
        updatedCommentDto.setAuthorId(2L);
        Comment comment = prepareComment();
        when(commentRepository.findById(updatedCommentDto.getId())).thenReturn(Optional.of(comment));

        assertThrows(DataValidationException.class, () -> commentService.updateComment(updatedCommentDto));
    }

    @Test
    public void testUpdateCommentSuccessful() {
        UpdatedCommentDto updatedCommentDto = prepareUpdateCommentDto();
        Comment comment = prepareComment();
        Comment updatedComment = prepareUpdateComment();
        when(commentRepository.findById(updatedCommentDto.getId())).thenReturn(Optional.of(comment));

        commentService.updateComment(updatedCommentDto);

        verify(commentRepository, times(1)).save(updatedComment);
    }

    @Test
    public void testGetAllCommentsPostIdIfPostDoesNotExist() {
        Long postId = 111L;
        when(postRepository.existsById(postId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> commentService.getAllCommentsByPostIdSortedByCreatedDate(postId));
    }

    @Test
    public void testGetAllCommentsPostIdSuccessful() {
        Long postId = 10L;
        List<Comment> comments = prepareCommentList();
        List<Comment> sortedComments = List.of(
                prepareCommentList().get(0),
                prepareCommentList().get(2),
                prepareCommentList().get(1));
        List<CommentDto> sortedCommentDtoList = List.of(
                prepareCommentDtoList().get(0),
                prepareCommentDtoList().get(2),
                prepareCommentDtoList().get(1));
        when(postRepository.existsById(postId)).thenReturn(true);
        when(commentRepository.findAllByPostId(postId)).thenReturn(comments);
        when(commentMapper.toDtoList(sortedComments)).thenReturn(sortedCommentDtoList);

        List<CommentDto> testResultSortedCommentDtoList = commentService.getAllCommentsByPostIdSortedByCreatedDate(postId);

        assertEquals(testResultSortedCommentDtoList, sortedCommentDtoList);
    }

    @Test
    public void testDeleteCommentIsSuccessful() {
        Long commentId = 2L;

        commentService.deleteComment(commentId);

        verify(commentRepository, times(1)).deleteById(commentId);
    }
}