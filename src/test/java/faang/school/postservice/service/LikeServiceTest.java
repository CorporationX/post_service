package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.dto.kafka.KafkaKey;
import faang.school.postservice.exception.DataNotFoundException;
import faang.school.postservice.exception.SameTimeActionException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.kafka.producer.KafkaLikeProducer;
import faang.school.postservice.service.redis.LikeEventPublisher;
import faang.school.postservice.validator.LikeValidator;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostService postService;
    @Mock
    private CommentService commentService;
    @Mock
    private LikeEventPublisher likeEventPublisher;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private KafkaLikeProducer kafkaLikeProducer;
    @Mock
    private CommentMapper commentMapper;
    @Spy
    private LikeMapperImpl likeMapper;
    @Spy
    private PostMapperImpl postMapper;
    @InjectMocks
    private LikeService likeService;

    private LikeValidator likeValidator;
    private LikeDto likeDto;
    private UserDto userDto;
    private Like like;
    private PostDto postDto;
    private CommentDto commentDto;
    private Comment comment;
    private final Long USER_ID = 1L;
    private final Long POST_ID = 1L;
    private final Long COMMENT_ID = 1L;

    @BeforeEach
    void setUp() {
        likeDto = LikeDto.builder()
                .id(0L)
                .userId(USER_ID)
                .build();
        userDto = UserDto.builder()
                .id(USER_ID)
                .username("Andrey")
                .email("gmail@gmail.com")
                .build();
        postDto = PostDto.builder()
                .id(POST_ID)
                .authorId(2L)
                .build();
        like = Like.builder()
                .id(0L)
                .userId(USER_ID)
                .post(postMapper.toEntity(postDto))
                .build();
        commentDto = CommentDto
                .builder()
                .id(COMMENT_ID)
                .build();
        comment = Comment.builder()
                .id(COMMENT_ID)
                .build();
        likeValidator = new LikeValidator(userServiceClient);
        likeService = new LikeService(likeValidator, likeMapper, likeRepository, postService, commentService,
                postMapper,likeEventPublisher, kafkaLikeProducer, commentMapper);
    }

    @Test
    void testLikePost() {
        likeDto.setPostId(POST_ID);
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(postService.getPost(POST_ID)).thenReturn(postDto);

        assertEquals(likeMapper.toDto(like), likeService.likePost(likeDto));
        verify(kafkaLikeProducer).sendMessage(KafkaKey.CREATE, likeDto);
        verify(likeRepository).save(like);
        verify(likeEventPublisher).publish(like);
    }

    @Test
    void testWhenUserDoesNotExistOnLikingPost() {
        when(userServiceClient.getUser(USER_ID)).thenThrow(FeignException.class);
        assertThrows(DataNotFoundException.class, () -> likeService.likePost(likeDto));
    }

    @Test
    void testWhenAddLikeOnPostAndComment() {
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        likeDto.setCommentId(COMMENT_ID);
        likeDto.setPostId(POST_ID);
        assertThrows(SameTimeActionException.class, () -> likeService.likePost(likeDto));
    }

    @Test
    void testUnlikePost() {
        likeService.unlikePost(POST_ID, USER_ID);
        verify(likeRepository).deleteByPostIdAndUserId(POST_ID, USER_ID);
    }

    @Test
    void testLikeComment() {
        likeDto.setCommentId(COMMENT_ID);
        like.setComment(comment);
        like.setPost(null);

        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(commentService.getComment(COMMENT_ID)).thenReturn(commentDto);
        when(commentService.getComment(COMMENT_ID)).thenReturn(commentDto);
        when(commentMapper.toEntity(commentDto)).thenReturn(comment);

        assertEquals(likeMapper.toDto(like), likeService.likeComment(likeDto));
        verify(likeRepository).save(like);
        verify(kafkaLikeProducer).sendMessage(KafkaKey.CREATE, likeDto);
    }

    @Test
    void testWhenUserDoesNotExistOnLikingComment() {
        when(userServiceClient.getUser(USER_ID)).thenThrow(FeignException.class);
        assertThrows(DataNotFoundException.class, () -> likeService.likeComment(likeDto));
    }

    @Test
    void testUnlikeComment() {
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.of(like));
        when(commentService.getComment(COMMENT_ID)).thenReturn(commentDto);
        likeService.unlikeComment(COMMENT_ID, USER_ID);
        likeDto.setPostId(0L);

        verify(likeRepository).deleteByCommentIdAndUserId(COMMENT_ID, USER_ID);
        verify(kafkaLikeProducer).sendMessage(KafkaKey.DELETE, likeDto);
    }
}