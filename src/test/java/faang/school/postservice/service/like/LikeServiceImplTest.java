package faang.school.postservice.service.like;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.like.RedisLikeReceivedEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class LikeServiceImplTest {
    private Post post1;
    private Comment comment1;
    private Like likeComment;
    private Like likePost;

    @Captor
    private ArgumentCaptor<Like> likeCaptor;

    @Mock
    private PostRepository postRepository;
    
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikeRepository likeRepository;

    @Spy
    private LikeMapperImpl likeMapper;

    @Mock
    private UserContext userContext;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private RedisLikeReceivedEventPublisher likeReceivedPublisher;

    @InjectMocks
    private LikeServiceImpl likeService;

    @BeforeEach
    void setUp() {
        post1 = Post.builder()
            .id(1L)
            .content("Test Content")
            .build();
        
        comment1 = Comment.builder()
            .id(1L)
            .content("Test Content")
            .post(post1)
            .build();
        
        likeComment = Like.builder()
            .id(1L)
            .comment(comment1)
            .build();
        
        likePost = Like.builder()
            .id(1L)
            .post(post1)
            .build();
    
    }

    @Test
    public void testPutLikeToPost_whenLikeIsValid_thenLikeIsCreated() {
        when(postRepository.findById(post1.getId())).thenReturn(Optional.of(post1));
        when(likeRepository.findByUserId(1L)).thenReturn(List.of());
        when(userContext.getUserId()).thenReturn(1L);

        likeService.putLikeToPost(1L);

        verify(likeRepository, times(1)).save(likeCaptor.capture());
        Like capturedLike = likeCaptor.getValue();
        assertEquals(likePost.getPost(), capturedLike.getPost());
    }

    @Test
    public void testPutLikeToPost_whenPostDoesNotExist_thenThrow() {
        verify(likeRepository, never()).save(any());
        assertThrows(EntityNotFoundException.class, () -> likeService.putLikeToPost(1L));
    }

    @Test
    public void testPutLikeToComment_whenLikeIsValid_thenLikeIsCreated() {
        when(commentRepository.findById(comment1.getId())).thenReturn(Optional.of(comment1));
        when(likeRepository.findByUserId(1L)).thenReturn(List.of());
        when(userContext.getUserId()).thenReturn(1L);

        likeService.putLikeToComment(1L);

        verify(likeRepository, times(1)).save(likeCaptor.capture());
        Like capturedLike = likeCaptor.getValue();
        assertEquals(likeComment.getComment(), capturedLike.getComment());
    }

    @Test
    public void testPutLikeToComment_whenCommentDoesNotExist_thenThrow() {
        verify(likeRepository, never()).save(any());
        assertThrows(EntityNotFoundException.class, () -> likeService.putLikeToPost(1L));
    }

}
