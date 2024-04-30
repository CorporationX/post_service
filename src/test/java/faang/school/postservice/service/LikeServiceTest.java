package faang.school.postservice.service;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.event.LikeAddEvent;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.kafka.KafkaEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private LikeMapper likeMapper;

    @Mock
    private KafkaEventPublisher kafkaEventPublisher;

    @InjectMocks
    private LikeService likeService;

    private LikeDto likeDto;
    private Like like;
    private Comment comment;
    private Post post;
    LikeAddEvent likeAddEvent;

    @BeforeEach
    public void setUp() {
        likeDto = new LikeDto(1L, 1L, 1L, LocalDateTime.now());
        post = new Post();
        post.setId(1L);
        post.setAuthorId(1L);
        post.setCreatedAt(null);

        comment = new Comment();
        like = new Like();
        like.setId(1L);
        like.setUserId(1L);
        like.setComment(comment);
        like.setPost(post);

        likeAddEvent = new LikeAddEvent(like.getId(),
                post.getAuthorId(), post.getId(), post.getCreatedAt());
    }

    @Test
    public void testAddLikeWhenValidLikeDtoThenLikeAdded() {
        String likesTopic = null;
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(likeMapper.toEntity(any(LikeDto.class))).thenReturn(like);

        likeService.addLike(likeDto);

        verify(likeRepository, times(1)).save(any(Like.class));
        verify(kafkaEventPublisher, times(1)).sendEvent(likesTopic, likeAddEvent);
    }

    @Test
    public void testAddLikeWhenCommentNotFoundThenNotFoundException() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        try {
            likeService.addLike(likeDto);
        } catch (Exception e) {
            assert (e instanceof NotFoundException);
        }

        verify(likeRepository, times(0)).save(any(Like.class));
    }

    @Test
    public void testAddLikeWhenPostNotFoundThenNotFoundException() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        try {
            likeService.addLike(likeDto);
        } catch (Exception e) {
            assert (e instanceof NotFoundException);
        }

        verify(likeRepository, times(0)).save(any(Like.class));
    }
}