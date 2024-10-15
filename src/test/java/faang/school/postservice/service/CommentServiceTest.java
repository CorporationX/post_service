package faang.school.postservice.service;

import faang.school.postservice.cache.model.CommentRedis;
import faang.school.postservice.cache.service.UserRedisService;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.kafka.event.comment.CommentAddedEvent;
import faang.school.postservice.kafka.producer.KafkaProducer;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CommentService.class)
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
    private CommentDto commentDto;
    private Comment comment;
    private UserDto userDto;
    private int batchSize;

    @MockBean
    private CommentMapper mapper;
    @MockBean
    private KafkaProducer kafkaProducer;
    @MockBean
    private PostRepository postRepository;
    @MockBean
    private CommentRepository commentRepository;
    @MockBean
    private UserServiceClient userServiceClient;
    @MockBean
    private UserRedisService userRedisService;
    @Autowired
    private CommentService service;

    @Value("${spring.kafka.topic.comment.added}")
    private String commentAddedTopic;

    @BeforeEach
    void setUp() {
        //Arrange
        post = new Post();
        post.setId(VALID_ID_IN_DB);
        commentDto = new CommentDto();
        commentDto.setAuthorId(VALID_ID_IN_DB);
        commentDto.setContent(RANDOM_VALID_STRING);
        commentDto.setId(VALID_ID_IN_DB);
        comment = new Comment();
        post.setComments(List.of(comment));
        comment.setId(VALID_ID_IN_DB);
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());
        userDto = UserDto.builder().id(VALID_ID_IN_DB).build();
        batchSize = 5;
    }

    @Test
    public void testAddCommentWhenPostNotFound() {
        //Act
        when(postRepository.findById(INVALID_ID_IN_DB)).thenReturn(Optional.empty());
        //Assert
        assertEquals(MESSAGE_POST_NOT_IN_DB,
                assertThrows(RuntimeException.class,
                        () -> service.addComment(INVALID_ID_IN_DB, new CommentDto())).getMessage());
    }

    @Test
    public void testAddCommentWithInvalidAuthor() {
        // Arrange
        doThrow(FeignException.class).when(userServiceClient).getUser(commentDto.getAuthorId());
        when(postRepository.findById(VALID_ID_IN_DB)).thenReturn(Optional.of(post));
        //Assert
        assertThrows(RuntimeException.class, () -> service.addComment(VALID_ID_IN_DB, commentDto));
    }

    @Test
    public void testAddCommentWhenContentIsEmpty() {
        //Arrange
        commentDto.setContent(EMPTY_CONTENT);
        //Act
        when(postRepository.findById(VALID_ID_IN_DB)).thenReturn(Optional.of(post));
        //Assert
        assertEquals(MESSAGE_INVALID_TEXT_OF_COMMENT,
                assertThrows(RuntimeException.class,
                        () -> service.addComment(VALID_ID_IN_DB, commentDto)).getMessage());
    }

    @Test
    public void testAddCommentWhenContentIsBlank() {
        //Arrange
        commentDto.setContent(BLANK_CONTENT);
        //Act
        when(postRepository.findById(VALID_ID_IN_DB)).thenReturn(Optional.of(post));
        //Assert
        assertEquals(MESSAGE_INVALID_TEXT_OF_COMMENT,
                assertThrows(RuntimeException.class,
                        () -> service.addComment(VALID_ID_IN_DB, commentDto)).getMessage());
    }

    @Test
    public void testAddCommentWhenInvalidLengthContent() {
        //Arrange
        commentDto.setContent(new String(new char[INVALID_LENGTH_OF_CONTENT]));
        //Act
        when(postRepository.findById(VALID_ID_IN_DB)).thenReturn(Optional.of(post));
        //Assert
        assertEquals(MESSAGE_INVALID_TEXT_OF_COMMENT,
                assertThrows(RuntimeException.class,
                        () -> service.addComment(VALID_ID_IN_DB, commentDto)).getMessage());
    }

    @Test
    public void testAddComment() {
        // Arrange
        CommentAddedEvent event = new CommentAddedEvent();
        when(postRepository.findById(VALID_ID_IN_DB)).thenReturn(Optional.of(post));
        when(userServiceClient.getUser(VALID_ID_IN_DB)).thenReturn(userDto);
        when(mapper.toEntity(commentDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(mapper.toCommentEvent(comment)).thenReturn(event);
        when(mapper.toDto(comment)).thenReturn(commentDto);

        //Act
        CommentDto actual = service.addComment(post.getId(), commentDto);
        //Assert
        verify(commentRepository,times(1)).save(comment);
        verify(userRedisService, times(1)).save(userDto);
        verify(kafkaProducer, times(1)).send(commentAddedTopic, event);
        verify(mapper, times(1)).toDto(Mockito.any());
        assertEquals(commentDto, actual);
    }

    @Test
    public void testGetCommentFromDbWithInvalidId() {
        //Arrange
        commentDto.setId(INVALID_ID_IN_DB);
        //Act
        when(commentRepository.findById(INVALID_ID_IN_DB)).thenReturn(Optional.empty());
        //Assert
        assertEquals(MESSAGE_COMMENT_NOT_EXIST,
                assertThrows(RuntimeException.class,
                        () -> service.changeComment(INVALID_ID_IN_DB, commentDto)).getMessage());
    }

    @Test
    public void testPostIdAndCommentPostIdNotEqual() {
        //Act
        when(commentRepository.findById(VALID_ID_IN_DB)).thenReturn(Optional.of(comment));
        //Assert
        assertEquals(MESSAGE_POST_ID_AND_COMMENT_POST_ID_NOT_EQUAL,
                assertThrows(RuntimeException.class,
                        () -> service.changeComment(INVALID_ID_IN_DB, commentDto)).getMessage());
    }

    @Test
    public void testVerifyServiceChangeComment() {
        //Act
        when(commentRepository.findById(VALID_ID_IN_DB)).thenReturn(Optional.of(comment));
        //Assert
        service.changeComment(post.getId(), commentDto);
        verify(mapper).toDto(Mockito.any());
    }

    @Test
    public void testVerifyGetAllCommentsOfPost() {
        //Arrange
        List<Comment> comments = List.of(comment);
        //Act
        when(commentRepository.findAllByPostId(VALID_ID_IN_DB)).thenReturn(comments);
        //Assert
        service.getAllCommentsOfPost(post.getId());
        verify(mapper, Mockito.times(comments.size())).toDto(comment);
    }

    @Test
    public void testInvalidCommentForDelete() {
        //Act
        when(postRepository.findById(VALID_ID_IN_DB)).thenReturn(Optional.of(post));
        //Assert
        assertEquals(MESSAGE_COMMENT_NOT_EXIST,
                assertThrows(RuntimeException.class,
                        () -> service.deleteComment(post.getId(), RANDOM_LONG)).getMessage());
    }

    @Test
    public void testVerifyDeleteComment() {
        //Act
        when(postRepository.findById(VALID_ID_IN_DB)).thenReturn(Optional.of(post));
        //Assert
        service.deleteComment(post.getId(), comment.getId());
        verify(mapper).toDto(comment);
    }

    @Test
    public void testFindLastBatchByPostId() {
        List<Comment> comments = List.of(comment);
        CommentRedis commentRedis = CommentRedis.builder().id(comment.getId()).build();
        TreeSet<CommentRedis> redisComments = new TreeSet<>(Set.of(commentRedis));
        when(commentRepository.findLastBatchByPostId(batchSize, post.getId())).thenReturn(comments);
        when(mapper.toRedisTreeSet(comments)).thenReturn(redisComments);

        TreeSet<CommentRedis> actual = service.findLastBatchByPostId(batchSize, post.getId());

        verify(commentRepository, times(1)).findLastBatchByPostId(batchSize, post.getId());
        verify(mapper, times(1)).toRedisTreeSet(comments);
        assertEquals(redisComments, actual);
    }

    @Test
    void testFindLastBatchByPostIds() {
        List<Long> postIds = List.of(post.getId());
        List<Comment> comments = post.getComments();
        List<CommentRedis> redisComments = List.of(CommentRedis.builder().id(comment.getId()).build());
        when(commentRepository.findLastBatchByPostIds(batchSize, postIds)).thenReturn(comments);
        when(mapper.toRedis(comments)).thenReturn(redisComments);

        List<CommentRedis> actual = service.findLastBatchByPostIds(batchSize, postIds);

        verify(commentRepository, times(1)).findLastBatchByPostIds(batchSize, postIds);
        verify(mapper, times(1)).toRedis(comments);
        assertEquals(redisComments, actual);
    }
}