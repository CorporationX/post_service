package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeViewDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validation.LikeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private LikeMapper likeMapper;
    @Mock
    private PostService postService;
    @Mock
    private LikeValidator likeValidator;
    @Mock
    private CommentService commentService;
    @Mock
    private LikeNotificationService likeNotificationService;

    @InjectMocks
    private LikeService likeService;

    private final Like likeEntity = new Like();
    private final Post postEntity = new Post();
    private final LikeViewDto likeViewDto = new LikeViewDto();
    private final Comment commentEntity = new Comment();
    private final long postId = 1L;
    private final long commentId = 1L;
    private final long userId = 1L;

    @BeforeEach
    public void setUp() {
        likeViewDto.setUserId(userId);
    }

    @Test
    @DisplayName("Добавление лайка к посту - успешный сценарий")
    public void givenValidPostAndUser_WhenLikePost_ThenReturnLikeViewDto() {
        likeViewDto.setPostId(postId);
        when(postService.getPostEntity(postId)).thenReturn(postEntity);
        when(likeRepository.save(any(Like.class))).thenReturn(likeEntity);
        when(likeMapper.toDto(likeEntity)).thenReturn(likeViewDto);

        LikeViewDto result = likeService.likePost(postId, userId);
        assertNotNull(result);

        verify(likeValidator).validateForAddingPostLike(postId, userId);
        verify(postService).getPostEntity(postId);
        verify(likeRepository).save(any(Like.class));
        verify(likeNotificationService).publishUserLikeEvent(postEntity, userId);
        verify(likeMapper).toDto(likeEntity);
    }

    @Test
    @DisplayName("Удаление лайка с поста - успешный сценарий")
    public void givenValidPostAndUser_WhenUnlikePost_ThenLikeIsDeleted() {
        likeService.unlikePost(postId, userId);

        verify(likeValidator, times(1))
                .validateForRemovingPostLike(postId, userId);
        verify(likeRepository, times(1))
                .deleteByPostIdAndUserId(postId, userId);
    }

    @Test
    @DisplayName("Добавление лайка к комментарию - успешный сценарий")
    public void givenValidPostAndUser_WhenLikeComment_ThenReturnLikeViewDto() {
        likeViewDto.setCommentId(commentId);
        when(commentService.getCommentById(commentId)).thenReturn(commentEntity);
        when(likeRepository.save(any(Like.class))).thenReturn(likeEntity);
        when(likeMapper.toDto(likeEntity)).thenReturn(likeViewDto);

        LikeViewDto result = likeService.likeComment(commentId, userId);
        assertNotNull(result);

        verify(likeValidator).validateForAddingCommentLike(commentId, userId);
        verify(commentService).getCommentById(commentId);
        verify(likeRepository).save(any(Like.class));
        verify(likeMapper).toDto(likeEntity);
    }

    @Test
    @DisplayName("Удаление лайка с комментария - успешный сценарий")
    public void givenValidPostAndUser_WhenUnlikeComment_ThenLikeIsDeleted() {
        likeService.unlikeComment(commentId, userId);

        verify(likeValidator, times(1))
                .validateForRemovingCommentLike(commentId, userId);
        verify(likeRepository, times(1))
                .deleteByCommentIdAndUserId(commentId, userId);
    }
}
