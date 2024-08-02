package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    private static final String MESSAGE_POST_NOT_IN_DB = "Post is not in the database";
    private static final String MESSAGE_INVALID_TEXT_OF_COMMENT = "Invalid content of comment";
    private static final String MESSAGE_COMMENT_NOT_EXIST = "This comment does not exist";
    private static final String MESSAGE_POST_ID_AND_COMMENT_POST_ID_NOT_EQUAL = "postId and commentPostId not equal";
    private static final long INVALID_ID_IN_DB = 2L;
    private static final long VALID_ID_IN_DB = 1L;
    private static final String EMPTY_CONTENT = "";
    private static final String BLANK_CONTENT = "   ";
    private static final int INVALID_LENGTH_OF_CONTENT = 5000;
    private static final String RANDOM_VALID_STRING = "Random valid string";
    private static final long RANDOM_LONG = 5L;

    private Post post;
    private CommentDto dto;
    private Comment comment;

    @Mock
    private CommentMapper mapper;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private CommentService service;

    @BeforeEach
    void setUp() {
        //Arrange
        post = new Post();
        post.setId(VALID_ID_IN_DB);
        dto = new CommentDto();
        dto.setAuthorId(VALID_ID_IN_DB);
        dto.setContent(RANDOM_VALID_STRING);
        dto.setId(VALID_ID_IN_DB);
        comment = new Comment();
        post.setComments(List.of(comment));
        comment.setId(VALID_ID_IN_DB);
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    public void testGetPostWithInvalidId() {
        //Act
        Mockito.when(postRepository.findById(INVALID_ID_IN_DB)).thenReturn(Optional.empty());
        //Assert
        assertEquals(MESSAGE_POST_NOT_IN_DB,
                assertThrows(RuntimeException.class,
                        () -> service.addComment(INVALID_ID_IN_DB, new CommentDto())).getMessage());
    }

    @Test
    public void testEmptyContentComment() {
        //Arrange
        dto.setContent(EMPTY_CONTENT);
        //Act
        Mockito.when(postRepository.findById(VALID_ID_IN_DB)).thenReturn(Optional.of(post));
        //Assert
        assertEquals(MESSAGE_INVALID_TEXT_OF_COMMENT,
                assertThrows(RuntimeException.class,
                        () -> service.addComment(VALID_ID_IN_DB, dto)).getMessage());
    }

    @Test
    public void testBlankContentComment() {
        //Arrange
        dto.setContent(BLANK_CONTENT);
        //Act
        Mockito.when(postRepository.findById(VALID_ID_IN_DB)).thenReturn(Optional.of(post));
        //Assert
        assertEquals(MESSAGE_INVALID_TEXT_OF_COMMENT,
                assertThrows(RuntimeException.class,
                        () -> service.addComment(VALID_ID_IN_DB, dto)).getMessage());
    }

    @Test
    public void testContentInvalidLengthComment() {
        //Arrange
        dto.setContent(new String(new char[INVALID_LENGTH_OF_CONTENT]));
        //Act
        Mockito.when(postRepository.findById(VALID_ID_IN_DB)).thenReturn(Optional.of(post));
        //Assert
        assertEquals(MESSAGE_INVALID_TEXT_OF_COMMENT,
                assertThrows(RuntimeException.class,
                        () -> service.addComment(VALID_ID_IN_DB, dto)).getMessage());
    }

    @Test
    public void testVerifyServiceAddComment() {
        //Act
        Mockito.when(postRepository.findById(VALID_ID_IN_DB)).thenReturn(Optional.of(post));
        Mockito.when(mapper.toEntity(dto)).thenReturn(comment);
        //Assert
        service.addComment(post.getId(), dto);
        Mockito.verify(mapper).toDto(Mockito.any());
    }

    @Test
    public void testGetCommentFromDbWithInvalidId() {
        //Arrange
        dto.setId(INVALID_ID_IN_DB);
        //Act
        Mockito.when(commentRepository.findById(INVALID_ID_IN_DB)).thenReturn(Optional.empty());
        //Assert
        assertEquals(MESSAGE_COMMENT_NOT_EXIST,
                assertThrows(RuntimeException.class,
                        () -> service.changeComment(INVALID_ID_IN_DB, dto)).getMessage());
    }

    @Test
    public void testPostIdAndCommentPostIdNotEqual() {
        //Act
        Mockito.when(commentRepository.findById(VALID_ID_IN_DB)).thenReturn(Optional.of(comment));
        //Assert
        assertEquals(MESSAGE_POST_ID_AND_COMMENT_POST_ID_NOT_EQUAL,
                assertThrows(RuntimeException.class,
                        () -> service.changeComment(INVALID_ID_IN_DB, dto)).getMessage());
    }

    @Test
    public void testVerifyServiceChangeComment() {
        //Act
        Mockito.when(commentRepository.findById(VALID_ID_IN_DB)).thenReturn(Optional.of(comment));
        //Assert
        service.changeComment(post.getId(), dto);
        Mockito.verify(mapper).toDto(Mockito.any());
    }

    @Test
    public void testVerifyGetAllCommentsOfPost() {
        //Arrange
        List<Comment> comments = List.of(comment);
        //Act
        Mockito.when(commentRepository.findAllByPostId(VALID_ID_IN_DB)).thenReturn(comments);
        //Assert
        service.getAllCommentsOfPost(post.getId());
        Mockito.verify(mapper, Mockito.times(comments.size())).toDto(comment);
    }

    @Test
    public void testInvalidCommentForDelete() {
        //Act
        Mockito.when(postRepository.findById(VALID_ID_IN_DB)).thenReturn(Optional.of(post));
        //Assert
        assertEquals(MESSAGE_COMMENT_NOT_EXIST,
                assertThrows(RuntimeException.class,
                        () -> service.deleteComment(post.getId(), RANDOM_LONG)).getMessage());
    }

    @Test
    public void testVerifyDeleteComment() {
        //Act
        Mockito.when(postRepository.findById(VALID_ID_IN_DB)).thenReturn(Optional.of(post));
        //Assert
        service.deleteComment(post.getId(), comment.getId());
        Mockito.verify(mapper).toDto(comment);
    }
}