package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.kafka.LikeProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.LikeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {


    @Mock
    private LikeValidator likeValidator;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private LikeMapper likeMapper;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private LikeProducer likeProducer;
    @InjectMocks
    private LikeServiceImpl likeService;

    private LikeDto likeDto;
    private Like like;

    @BeforeEach
    public void setUp() {
        likeDto = LikeDto.builder()
                .userId(2L)
                .build();
        like = Like.builder()
                .build();
    }

    @Test
    public void whenLikeCommentSuccessfully() {
        likeDto.setCommentId(1L);
        when(likeRepository.findByCommentIdAndUserId(likeDto.getCommentId(), likeDto.getUserId())).thenReturn(Optional.empty());
        when(likeMapper.toEntity(likeDto)).thenReturn(like);
        when(commentRepository.findById(likeDto.getCommentId())).thenReturn(Optional.of(new Comment()));
        when(likeRepository.save(like)).thenReturn(like);
        when(likeMapper.toDto(like)).thenReturn(likeDto);
        LikeDto result = likeService.likeComment(likeDto);
        assertNotNull(result);
        verify(likeValidator).validate(likeDto);
    }

    @Test
    public void whenLikeCommentThenLikeWillNotBeSaved() {
        likeDto.setCommentId(1L);
        when(likeRepository.findByCommentIdAndUserId(likeDto.getCommentId(), likeDto.getUserId())).thenReturn(Optional.of(like));
        LikeDto result = likeService.likeComment(likeDto);
        assertNull(result);
        verify(likeValidator).validate(likeDto);
        verifyNoMoreInteractions(likeRepository);
    }

    @Test
    public void whenLikePostSuccessfully() {
        likeDto.setPostId(1L);
        when(likeRepository.findByPostIdAndUserId(likeDto.getPostId(), likeDto.getUserId())).thenReturn(Optional.empty());
        when(likeMapper.toEntity(likeDto)).thenReturn(like);
        when(postRepository.findById(likeDto.getPostId())).thenReturn(Optional.of(new Post()));
        when(likeRepository.save(like)).thenReturn(like);
        when(likeMapper.toDto(like)).thenReturn(likeDto);
        LikeDto result = likeService.likePost(likeDto);
        assertNotNull(result);
        verify(likeValidator).validate(likeDto);
    }

    @Test
    public void whenLikePostThenLikeWillNotBeSaved() {
        likeDto.setPostId(1L);
        when(likeRepository.findByPostIdAndUserId(likeDto.getPostId(), likeDto.getUserId())).thenReturn(Optional.of(like));
        LikeDto result = likeService.likePost(likeDto);
        assertNull(result);
        verify(likeValidator).validate(likeDto);
        verifyNoMoreInteractions(likeRepository);
    }
}