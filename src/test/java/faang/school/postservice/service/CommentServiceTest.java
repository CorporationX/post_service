package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.util.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static faang.school.postservice.exception.MessagesForCommentsException.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PostService postService;

    @Spy
    private TestData testData;

    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Captor
    private ArgumentCaptor<Comment> captor;

    private Comment comment;

    private CommentDto commentDto;

    private PostDto postDto;

    private long postId;

    private List<Comment> comments;

    @BeforeEach
    void init() {
        comment = testData.returnComment();
        commentDto = commentMapper.ToDto(testData.returnComment());
        postDto = testData.returnPostDto();
        postId = postDto.getId();
        comments = testData.returnListOfComments();
    }

    @Test
    void testForCreateCommentWithNoUserInDB() {
        when(userServiceClient.getUser(commentDto.getAuthorId())).thenReturn(null);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> commentService.createComment(testData.returnPostDto().getId(), commentDto));
        assertEquals(NO_USER_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    void testForCreateCommentAndSave() {
        when(userServiceClient.getUser(commentDto.getAuthorId())).thenReturn(new UserDto());
        when(postService.getPostById(postId)).thenReturn(postDto);

        commentService.createComment(postId, commentDto);

        verify(commentRepository).save(captor.capture());
        Comment comment = captor.getValue();

        assertEquals(commentDto.getAuthorId(), comment.getAuthorId());
        assertEquals(commentDto.getContent(), comment.getContent());
    }

    @Test
    void testForUpdateComment() {
        when(userServiceClient.getUser(commentDto.getAuthorId())).thenReturn(null);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> commentService.updateComment(commentDto));
        assertEquals(NO_USER_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    void testForUpdateCommentIfNoCommentIdInCommentDto() {
        commentDto.setId(null);

        when(userServiceClient.getUser(commentDto.getAuthorId())).thenReturn(new UserDto());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> commentService.updateComment(commentDto));
        assertEquals(ID_IS_NULL.getMessage(), exception.getMessage());
    }

    @Test
    void testUpdateCommentIfNoCommentInDB() {
        when(userServiceClient.getUser(commentDto.getAuthorId())).thenReturn(new UserDto());
        when(commentRepository.findById(commentDto.getId())).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> commentService.updateComment(commentDto));
        assertEquals(NO_COMMENT_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    void testForUpdateCommentWithUpdate() {
        when(userServiceClient.getUser(commentDto.getAuthorId())).thenReturn(new UserDto());
        when(commentRepository.findById(commentDto.getId())).thenReturn(Optional.of(comment));

        commentService.updateComment(commentDto);

        verify(commentRepository).save(captor.capture());
        assertEquals(captor.getValue().getContent(), commentDto.getContent());
    }

    @Test
    void testForGetAllCommentsWithNoComments() {
        when(postService.getPostById(postId)).thenReturn(postDto);
        when(commentRepository.findAllByPostId(postId)).thenReturn(null);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> commentService.getAllComments(postId));
        assertEquals(NO_COMMENTS_IN_THE_POST.getMessage(), exception.getMessage());
    }

    @Test
    void testForGetAllCommentsWithSort() {
        when(postService.getPostById(postId)).thenReturn(postDto);
        when(commentRepository.findAllByPostId(postId)).thenReturn(comments);

        assertEquals(commentMapper.ToDtoList(comments), commentService.getAllComments(postId));
    }

    @Test
    void testForDeleteComment() {
        when(userServiceClient.getUser(commentDto.getAuthorId())).thenReturn(new UserDto());
        when(commentRepository.findById(commentDto.getId())).thenReturn(Optional.of(comment));

        commentService.deleteComment(commentDto);

        verify(commentRepository).delete(captor.capture());
        assertEquals(captor.getValue().getContent(), commentDto.getContent());
    }
}
