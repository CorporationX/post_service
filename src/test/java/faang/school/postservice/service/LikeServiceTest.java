package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.kafka.event.like.LikeAddedEvent;
import faang.school.postservice.kafka.producer.KafkaProducer;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = LikeService.class)
@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    private static final long INVALID_ID_IN_DB = 2L;
    private static final long VALID_ID_IN_DB = 1L;
    private static final String MESSAGE_POST_NOT_IN_DB = "Post is not in the database";
    private static final String AUTHOR_LIKE_NOT_IN_DB = "The author of the like is not in the database";
    private static final String MESSAGE_ALREADY_LIKED = "Already liked";
    private static final String MESSAGE_COMMENT_ABSENT = "Comment with this Id absent";
    private static final String MESSAGE_LIKE_NOT_PRESENT = "Like is not present";

    private Post post;
    private LikeDto dto;
    private Like like;
    @Value("${spring.kafka.topic.like.added}")
    private String likeAddedTopic;

    @MockBean
    private KafkaProducer kafkaProducer;
    @MockBean
    private PostRepository postRepository;
    @MockBean
    private UserServiceClient userServiceClient;
    @MockBean
    private LikeRepository likeRepository;
    @MockBean
    private LikeMapper mapper;
    @Autowired
    private LikeService service;

    @BeforeEach
    void setUp() {
        //Arrange
        dto = new LikeDto();
        dto.setUserId(VALID_ID_IN_DB);

        Comment comment = new Comment();
        comment.setId(VALID_ID_IN_DB);


        post = new Post();
        post.setId(VALID_ID_IN_DB);
        post.setComments(List.of(comment));

        like = new Like();
        like.setId(VALID_ID_IN_DB);
        like.setUserId(VALID_ID_IN_DB);
        like.setComment(comment);

        post.setLikes(List.of(like));
        comment.setLikes(List.of(like));

    }

    @Test
    public void testUserNotInDb() {
        //Act
        when(userServiceClient.getUser(Mockito.anyLong())).thenThrow(new RuntimeException(AUTHOR_LIKE_NOT_IN_DB));
        //Assert
        assertEquals(AUTHOR_LIKE_NOT_IN_DB,
                assertThrows(RuntimeException.class,
                        () -> service.addPostLike(VALID_ID_IN_DB, dto)).getMessage());
    }

    @Test
    public void testPostNotInDb() {
        //Act
        when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        //Assert
        assertEquals(MESSAGE_POST_NOT_IN_DB,
                assertThrows(RuntimeException.class,
                        () -> service.addPostLike(INVALID_ID_IN_DB, dto)).getMessage());
    }

    @Test
    public void testAddPostLikeAndLikeOnPostPresent() {
        //Act
        when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(post));
        //Assert
        assertEquals(MESSAGE_ALREADY_LIKED,
                assertThrows(RuntimeException.class,
                        () -> service.addPostLike(VALID_ID_IN_DB, dto)).getMessage());
    }

    @Test
    public void testVerifyAddPostLike() {
        //Arrange
        like.setUserId(INVALID_ID_IN_DB);
        //Act
        when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(post));
        when(mapper.toEntity(Mockito.any())).thenReturn(like);
        when(likeRepository.save(Mockito.any())).thenReturn(like);
        when(mapper.toDto(like)).thenReturn(dto);

        LikeDto actual = service.addPostLike(VALID_ID_IN_DB, dto);
        //Assert
        verify(mapper).toDto(like);
        verify(kafkaProducer).send(likeAddedTopic, new LikeAddedEvent(post.getId()));
        assertEquals(dto, actual);
    }

    @Test
    public void testDeletePostLikeAndLikeOnPostNotPresent() {
        //Arrange
        like.setUserId(INVALID_ID_IN_DB);
        //Act
        when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(post));
        //Assert
        assertEquals(MESSAGE_LIKE_NOT_PRESENT,
                assertThrows(RuntimeException.class,
                        () -> service.deletePostLike(VALID_ID_IN_DB, dto)).getMessage());
    }

    @Test
    public void testVerifyDeletePostLike() {
        //Act
        when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(post));
        when(mapper.toEntity(Mockito.any())).thenReturn(like);
        service.deletePostLike(VALID_ID_IN_DB, dto);
        //Assert
        verify(mapper).toDto(like);
    }

    @Test
    public void testCommentAbsentInDb() {
        //Act
        when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(post));
        //Assert
        assertEquals(MESSAGE_COMMENT_ABSENT,
                assertThrows(RuntimeException.class,
                        () -> service.addCommentLike(VALID_ID_IN_DB, INVALID_ID_IN_DB, dto)).getMessage());
    }

    @Test
    public void testAddCommentLikeAndLikeOnCommentPresent() {
        //Act
        when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(post));
        //Assert
        assertEquals(MESSAGE_ALREADY_LIKED,
                assertThrows(RuntimeException.class,
                        () -> service.addCommentLike(VALID_ID_IN_DB, VALID_ID_IN_DB, dto)).getMessage());
    }

    @Test
    public void testVerifyAddCommentLike() {
        //Arrange
        like.setUserId(INVALID_ID_IN_DB);
        //Act
        when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(post));
        when(mapper.toEntity(Mockito.any())).thenReturn(like);
        when(likeRepository.save(Mockito.any())).thenReturn(like);
        service.addCommentLike(VALID_ID_IN_DB, VALID_ID_IN_DB, dto);
        //Assert
        verify(mapper).toDto(like);
    }

    @Test
    public void testDeleteCommentLikeAndLikeOnCommentNotPresent() {
        //Arrange
        like.setUserId(INVALID_ID_IN_DB);
        //Act
        when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(post));
        //Assert
        assertEquals(MESSAGE_LIKE_NOT_PRESENT,
                assertThrows(RuntimeException.class,
                        () -> service.deleteCommentLike(VALID_ID_IN_DB, VALID_ID_IN_DB, dto)).getMessage());
    }

    @Test
    public void testVerifyDeleteCommentLike() {
        //Act
        when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(post));
        when(mapper.toEntity(Mockito.any())).thenReturn(like);
        service.deleteCommentLike(VALID_ID_IN_DB, VALID_ID_IN_DB, dto);
        //Assert
        verify(mapper).toDto(like);
    }
}