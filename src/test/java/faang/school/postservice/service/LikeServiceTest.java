package faang.school.postservice.service;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.kafka.LikeAction;
import faang.school.postservice.dto.kafka.LikeEvent;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.KafkaLikeProducer;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.redis.LikeEventPublisher;
import faang.school.postservice.validator.LikeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private LikeValidator likeValidator;
    @Spy
    private LikeMapper likeMapper = new LikeMapperImpl();
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostService postService;
    @Mock
    private CommentService commentService;
    @Mock
    private LikeEventPublisher likeEventPublisher;
    @Mock
    private KafkaLikeProducer kafkaPublisher;
    @InjectMocks
    private LikeService likeService;

    private LikeDto likePostDto;
    private LikeDto likeCommentDto;

    private Like postLike;
    private Like commentLike;

    private Post post;
    private Comment comment;

    private final Long userId = 1L;
    private final Long postId = 1L;
    private final Long commentId = 1L;
    private final Long likeId = 1L;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(postId)
                .deleted(false)
                .build();
        comment = Comment.builder()
                .id(commentId)
                .post(post)
                .build();
        likePostDto = LikeDto.builder()
                .userId(userId)
                .postId(postId)
                .build();
        likeCommentDto = LikeDto.builder()
                .userId(userId)
                .commentId(commentId)
                .build();
        postLike = Like.builder()
                .id(likeId)
                .userId(userId)
                .post(post)
                .build();
        commentLike = Like.builder()
                .id(likeId)
                .userId(userId)
                .comment(comment)
                .build();
    }

    @Test
    void testLikePostFirstScenario() {
        LikeEvent event = LikeEvent.builder()
                .postId(postId)
                .authorId(userId)
                .commentId(null)
                .likeAction(LikeAction.ADD)
                .build();

        when(postService.findPostBy(postId)).thenReturn(post);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(postLike);

        LikeDto result = likeService.likePost(likePostDto);

        assertEquals(likeMapper.toDto(postLike), result);

        verify(likeValidator).validateLike(likePostDto);
        verify(postService).findPostBy(postId);
        verify(likeRepository).findByPostIdAndUserId(postId, userId);
        verify(likeRepository).save(any(Like.class));
        verify(likeEventPublisher).publish(postLike);
        verify(kafkaPublisher).publish(event);
    }

    @Test
    void testLikePostSecondScenario() {
        when(postService.findPostBy(postId)).thenReturn(post);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.of(postLike));

        LikeDto result = likeService.likePost(likePostDto);

        assertEquals(likeMapper.toDto(postLike), result);
        verify(postService).findPostBy(postId);
        verify(likeRepository).findByPostIdAndUserId(postId, userId);
    }

    @Test
    void unlikePostTest() {
        LikeEvent event = LikeEvent.builder()
                .postId(postId)
                .authorId(userId)
                .commentId(null)
                .likeAction(LikeAction.REMOVE)
                .build();

        likeService.unlikePost(postId, userId);

        verify(likeRepository).deleteByPostIdAndUserId(postId, userId);
        verify(kafkaPublisher).publish(event);
    }

    @Test
    void likeCommentFirstScenarioTest() {
        when(commentService.getComment(commentId)).thenReturn(comment);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(commentLike);

        LikeDto expected = LikeDto.builder()
                .id(likeId)
                .userId(userId)
                .commentId(commentId)
                .build();

        LikeEvent event = LikeEvent.builder()
                .postId(postId)
                .authorId(userId)
                .commentId(commentId)
                .likeAction(LikeAction.ADD)
                .build();

        LikeDto result = likeService.likeComment(likeCommentDto);

        assertEquals(expected, result);

        verify(likeValidator).validateLike(likeCommentDto);
        verify(commentService).getComment(commentId);
        verify(likeRepository).findByCommentIdAndUserId(commentId, userId);
        verify(likeRepository).save(any(Like.class));
        verify(kafkaPublisher).publish(event);
    }

    @Test
    void likeCommentSecondScenarioTest() {
        when(commentService.getComment(commentId)).thenReturn(comment);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.of(commentLike));

        LikeDto result = likeService.likeComment(likeCommentDto);

        assertEquals(likeMapper.toDto(commentLike), result);

        verify(likeValidator).validateLike(likeCommentDto);
        verify(commentService).getComment(commentId);
        verify(likeRepository).findByCommentIdAndUserId(commentId, userId);
    }

    @Test
    void unlikeCommentTest() {
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.of(commentLike));

        LikeEvent event = LikeEvent.builder()
                .postId(postId)
                .authorId(userId)
                .commentId(commentId)
                .likeAction(LikeAction.REMOVE)
                .build();

        likeService.unlikeComment(commentId, userId);

        verify(likeRepository).findByCommentIdAndUserId(commentId, userId);
        verify(kafkaPublisher).publish(event);
        verify(likeRepository).deleteByCommentIdAndUserId(commentId, userId);
    }
}