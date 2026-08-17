package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserViewDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaLikeProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.AfterCommitManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserServiceClient serviceClient;
    @Mock
    private AfterCommitManager commitManager;
    @Mock
    private KafkaLikeProducer producer;
    @Mock
    private LikeMapper mapper;
    @Mock
    private UserContext userContext;

    @InjectMocks
    private LikeServiceImpl likeService;

    @Test
    void getUsersWhoLikedPostSuccess() {
        Long postId = 1L;
        Long userId = 3L;
        Like like = Like.builder().userId(userId).build();
        UserViewDto userViewDto = UserViewDto.builder().id(userId).build();

        when(likeRepository.findByPostId(postId)).thenReturn(List.of(like));
        when(serviceClient.getUsersByIds(List.of(userId))).thenReturn(List.of(userViewDto));

        List<UserViewDto> result = likeService.getUsersWhoLikedPost(postId);

        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).id());
        verify(likeRepository).findByPostId(postId);
        verify(serviceClient).getUsersByIds(List.of(userId));
    }

    @Test
    void getUsersWhoLikedCommentSuccess() {
        Long commentId = 2L;
        Long userId = 3L;
        Like like = Like.builder().userId(userId).build();
        UserViewDto userViewDto = UserViewDto.builder().id(userId).build();

        when(likeRepository.findByCommentId(commentId))
                .thenReturn(List.of(like));
        when(serviceClient.getUsersByIds(List.of(userId)))
                .thenReturn(List.of(userViewDto));

        List<UserViewDto> result = likeService.getUsersWhoLikedComment(commentId);

        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).id());
    }

    @Test
    void getUsersWhoLikedPostShouldIgnoreLikesWithoutUserId() {
        Long postId = 1L;
        when(likeRepository.findByPostId(postId)).thenReturn(List.of(Like.builder().build()));
        when(serviceClient.getUsersByIds(List.of())).thenReturn(List.of());

        List<UserViewDto> result = likeService.getUsersWhoLikedPost(postId);

        assertEquals(List.of(), result);
        verify(serviceClient).getUsersByIds(List.of());
    }

    @Test
    void addLikeToPostShouldDoNothingWhenLikeAlreadyExists() {
        Long postId = 1L;
        Long userId = 2L;
        when(userContext.getUserId()).thenReturn(userId);
        when(likeRepository.existsByPostIdAndUserId(postId, userId)).thenReturn(true);

        likeService.addLikeToPost(postId);

        verify(postRepository, never()).existsById(postId);
        verify(likeRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void addLikeToPostShouldThrowWhenPostDoesNotExist() {
        Long postId = 1L;
        Long userId = 2L;
        when(userContext.getUserId()).thenReturn(userId);
        when(likeRepository.existsByPostIdAndUserId(postId, userId)).thenReturn(false);
        when(postRepository.existsById(postId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> likeService.addLikeToPost(postId));

        verify(likeRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void addLikeToPostShouldSaveMappedLikeAndScheduleEvent() {
        Long postId = 1L;
        Long userId = 2L;
        Post post = Post.builder().id(postId).build();
        Like like = Like.builder().userId(userId).post(post).build();
        when(userContext.getUserId()).thenReturn(userId);
        when(likeRepository.existsByPostIdAndUserId(postId, userId)).thenReturn(false);
        when(postRepository.existsById(postId)).thenReturn(true);
        when(postRepository.findPostOrThrow(postId)).thenReturn(post);
        when(mapper.toEntity(userId, post)).thenReturn(like);
        when(likeRepository.save(like)).thenReturn(like);

        likeService.addLikeToPost(postId);

        verify(likeRepository).save(like);
        verify(commitManager).executeAfterCommit(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void addLikeToCommentShouldSaveMappedLikeAndScheduleEvent() {
        Long commentId = 1L;
        Long userId = 2L;
        Comment comment = Comment.builder().id(commentId).build();
        Like like = Like.builder().userId(userId).comment(comment).build();
        when(userContext.getUserId()).thenReturn(userId);
        when(likeRepository.existsByCommentIdAndUserId(commentId, userId)).thenReturn(false);
        when(commentRepository.existsById(commentId)).thenReturn(true);
        when(commentRepository.findByIdOrThrow(commentId)).thenReturn(comment);
        when(mapper.toEntity(userId, comment)).thenReturn(like);
        when(likeRepository.save(like)).thenReturn(like);

        likeService.addLikeToComment(commentId);

        verify(likeRepository).save(like);
        verify(commitManager).executeAfterCommit(org.mockito.ArgumentMatchers.any());
    }
}
