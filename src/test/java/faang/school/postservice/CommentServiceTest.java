package faang.school.postservice;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.UpdatedCommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    UserServiceClient userServiceClient;

    @InjectMocks
    CommentService commentService;

    private final CreateCommentDto createCommentDto = new CreateCommentDto();
    private final CommentDto commentDto = new CommentDto();
    private final UpdatedCommentDto updatedCommentDto = new UpdatedCommentDto();
    private final Comment comment = new Comment();
    private final Post post = new Post();
    private final UserDto userDto = new UserDto();

    CreateCommentDto prepareCreateCommentDto() {
        createCommentDto.setContent("Test content");
        createCommentDto.setAuthorId(1L);
        createCommentDto.setPostId(1L);
        return createCommentDto;
    }

    CommentDto prepareCommentDto() {
        commentDto.setId(1L);
        commentDto.setContent("Test content");
        commentDto.setAuthorId(1L);
        commentDto.setPostId(1L);
        return commentDto;
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

    UserDto prepareUserDto() {
        userDto.setId(1L);
        return userDto;
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

    @Test
    public void testCreateCommentIfAuthorDoesNotFound() {
        CreateCommentDto createCommentDto = prepareCreateCommentDto();
        when(userServiceClient.getUser(createCommentDto.getAuthorId())).thenReturn(Mockito.isNull());

        assertThrows(RuntimeException.class, () -> commentService.createComment(createCommentDto));
    }

    @Test
    public void testCreateCommentSuccessful() {
        CreateCommentDto createCommentDto = prepareCreateCommentDto();
        Comment comment = prepareComment();
        UserDto userDto = prepareUserDto();
        when(userServiceClient.getUser(createCommentDto.getAuthorId())).thenReturn(userDto);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toEntity(createCommentDto)).thenReturn(comment);

        commentService.createComment(createCommentDto);

        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    public void testUpdateCommentIfCommentDoesNotFound() {
        UpdatedCommentDto updatedCommentDto = prepareUpdateCommentDto();
        when(commentRepository.findById(updatedCommentDto.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.updateComment(updatedCommentDto));
    }

    @Test
    public void testUpdateCommentSuccessful() {
        UpdatedCommentDto updatedCommentDto = prepareUpdateCommentDto();
        Comment updatedComment = prepareUpdateComment();
        when(commentRepository.findById(updatedCommentDto.getId())).thenReturn(Optional.of(updatedComment));

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
