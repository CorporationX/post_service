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
        Mockito.when(postRepository.findById(INVALID_ID_IN_DB)).thenReturn(Optional.empty());
        assertEquals(MESSAGE_POST_NOT_IN_DB,
                assertThrows(RuntimeException.class,
                        () -> service.addComment(INVALID_ID_IN_DB, new CommentDto())).getMessage());
    }

    @Test
    public void testEmptyContentComment() {
        Mockito.when(postRepository.findById(VALID_ID_IN_DB)).thenReturn(Optional.of(post));
        dto.setContent(EMPTY_CONTENT);
        assertEquals(MESSAGE_INVALID_TEXT_OF_COMMENT,
                assertThrows(RuntimeException.class,
                        () -> service.addComment(VALID_ID_IN_DB, dto)).getMessage());
    }

    @Test
    public void testBlankContentComment() {
        Mockito.when(postRepository.findById(VALID_ID_IN_DB)).thenReturn(Optional.of(post));
        dto.setContent(BLANK_CONTENT);
        assertEquals(MESSAGE_INVALID_TEXT_OF_COMMENT,
                assertThrows(RuntimeException.class,
                        () -> service.addComment(VALID_ID_IN_DB, dto)).getMessage());
    }

    @Test
    public void testContentInvalidLengthComment() {
        Mockito.when(postRepository.findById(VALID_ID_IN_DB)).thenReturn(Optional.of(post));
        dto.setContent(new String(new char[INVALID_LENGTH_OF_CONTENT]));
        assertEquals(MESSAGE_INVALID_TEXT_OF_COMMENT,
                assertThrows(RuntimeException.class,
                        () -> service.addComment(VALID_ID_IN_DB, dto)).getMessage());
    }

    @Test
    public void testVerifyServiceAddComment() {
        Mockito.when(postRepository.findById(VALID_ID_IN_DB)).thenReturn(Optional.of(post));
        Mockito.when(mapper.toEntity(dto)).thenReturn(comment);
        service.addComment(post.getId(), dto);
        Mockito.verify(mapper).toDto(Mockito.any());
    }

    @Test
    public void testGetCommentFromDbWithInvalidId() {
        Mockito.when(commentRepository.findById(INVALID_ID_IN_DB)).thenReturn(Optional.empty());
        dto.setId(INVALID_ID_IN_DB);
        assertEquals(MESSAGE_COMMENT_NOT_EXIST,
                assertThrows(RuntimeException.class,
                        () -> service.changeComment(INVALID_ID_IN_DB, dto)).getMessage());
    }

    @Test
    public void testPostIdAndCommentPostIdNotEqual() {
        Mockito.when(commentRepository.findById(VALID_ID_IN_DB)).thenReturn(Optional.of(comment));
        assertEquals(MESSAGE_POST_ID_AND_COMMENT_POST_ID_NOT_EQUAL,
                assertThrows(RuntimeException.class,
                        () -> service.changeComment(INVALID_ID_IN_DB, dto)).getMessage());
    }

    @Test
    public void testVerifyServiceChangeComment() {
        Mockito.when(commentRepository.findById(VALID_ID_IN_DB)).thenReturn(Optional.of(comment));
        service.changeComment(post.getId(), dto);
        Mockito.verify(mapper).toDto(Mockito.any());
    }

    @Test
    public void testVerifyGetAllCommentsOfPost() {
        List<Comment> comments = List.of(comment);
        Mockito.when(commentRepository.findAllByPostId(VALID_ID_IN_DB)).thenReturn(comments);
        service.getAllCommentsOfPost(post.getId());
        Mockito.verify(mapper, Mockito.times(comments.size())).toDto(comment);
    }

    @Test
    public void testInvalidCommentForDelete() {
        Mockito.when(postRepository.findById(VALID_ID_IN_DB)).thenReturn(Optional.of(post));
        assertEquals(MESSAGE_COMMENT_NOT_EXIST,
                assertThrows(RuntimeException.class,
                        () -> service.deleteComment(post.getId(), RANDOM_LONG)).getMessage());
    }

    @Test
    public void testVerifyDeleteComment() {
        Mockito.when(postRepository.findById(VALID_ID_IN_DB)).thenReturn(Optional.of(post));
        service.deleteComment(post.getId(), comment.getId());
        Mockito.verify(mapper).toDto(comment);
    }
}