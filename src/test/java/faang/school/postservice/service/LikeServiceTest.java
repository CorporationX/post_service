package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.ConcurrentLikeException;
import faang.school.postservice.exception.DuplicateEntityException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    private final Long firstId = 1L;

    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private UserServiceClient userClient;

    @Mock
    private LikeEventPublisher likeEventPublisher;

    @BeforeEach
    public void setUp() {
        likeService = new LikeService(likeRepository, postRepository,
                commentRepository, userContext, userClient, likeEventPublisher);
    }

    @Test
    public void testNegativePutLikeOnPostWhenUserNotFound() {
        assertThrows(NullPointerException.class, () -> likeService.putLikeOnPost(firstId));
    }

    @Test
    public void testNegativePutLikeOnPostWhenPostNotFound() {
        includeCheckUser();

        assertThrows(EntityNotFoundException.class, () -> likeService.putLikeOnPost(firstId));
    }

    @Test
    public void testNegativePutLikeOnPostWhenDuplicateLike() {
        includeCheckUser();
        includeSecondNegativeTestLikeOnPost();
        Optional<Like> like = Optional.ofNullable(createLike(firstId, null, null));
        when(likeRepository.findByPostIdAndUserId(firstId, firstId)).thenReturn(like);

        assertThrows(DuplicateEntityException.class, () -> likeService.putLikeOnPost(firstId));
    }

    @Test
    public void testNegativePutLikeOnPostWhenLikeAlreadyExists() {
        includeCheckUser();
        includeSecondNegativeTestLikeOnPost();
        Like like = createLike(firstId, null, null);
        Comment comment = createComment(firstId, List.of(like), null);
        Post post = createPost(firstId, List.of(comment), Collections.emptyList());
        when(likeRepository.findByPostIdAndUserId(firstId, firstId)).thenReturn(Optional.empty());
        when(postRepository.findById(firstId)).thenReturn(Optional.of(post));

        assertThrows(EntityNotFoundException.class, () -> likeService.putLikeOnPost(firstId));
    }

    @Test
    public void testPositivePutLikeOnPostSuccessful() {
        includeCheckUser();
        includeSecondNegativeTestLikeOnPost();
        Post post = createPost(firstId, Collections.emptyList(), Collections.emptyList());
        Like like = createLike(firstId, post, null);
        when(likeRepository.findByPostIdAndUserId(firstId, firstId)).thenReturn(Optional.of(like));
        LikeService likeServiceSpy = Mockito.spy(likeService);
        doReturn(true).when(likeServiceSpy).isLikeOnPostEmpty(firstId, firstId);


        likeServiceSpy.putLikeOnPost(firstId);

        verify(likeRepository, times(1)).save(like);
    }

    @Test
    public void testNegativeRemoveLikeAtPostWhenUserNotFound() {
        assertThrows(NullPointerException.class, () -> likeService.removeLikeAtPost(firstId));
    }

    @Test
    public void testNegativeRemoveLikeAtPostWhenIdInvalid() {
        includeCheckUser();

        assertThrows(EntityNotFoundException.class, () -> likeService.removeLikeAtPost(firstId));
    }

    @Test
    public void testNegativeRemoveLikeAtPostWhenPostNotFound() {
        includeCheckUser();
        when(likeRepository.findByPostIdAndUserId(firstId, firstId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> likeService.removeLikeAtPost(firstId));
    }

    @Test
    public void testPositiveRemoveLikeAtPostSuccessful() {
        Like like = createLike(firstId,
                createPost(firstId, Collections.emptyList(), Collections.emptyList()), null);
        includeCheckUser();
        when(likeRepository.findByPostIdAndUserId(firstId, firstId)).thenReturn(Optional.ofNullable(like));

        likeService.removeLikeAtPost(firstId);

        verify(likeRepository, times(1)).deleteByPostIdAndUserId(firstId, firstId);
    }

    @Test
    public void testNegativePutLikeOnCommentWhenUserNotFound() {
        assertThrows(NullPointerException.class, () -> likeService.putLikeOnComment(firstId));
    }

    @Test
    public void testNegativePutLikeOnCommentWhenCommentNotFound() {
        includeCheckUser();

        assertThrows(EntityNotFoundException.class, () -> likeService.putLikeOnComment(firstId));
    }

    @Test
    public void testNegativePutLikeOnCommentWhenDuplicateLike() {
        includeCheckUser();
        includeSecondNegativeTestLikeOnComment();
        Optional<Like> like = Optional.ofNullable(createLike(firstId, null, null));
        when(likeRepository.findByCommentIdAndUserId(firstId, firstId)).thenReturn(like);

        assertThrows(DuplicateEntityException.class, () -> likeService.putLikeOnComment(firstId));
    }

    @Test
    public void testNegativePutLikeOnCommentWhenLikeAlreadyExists() {
        includeCheckUser();
        includeSecondNegativeTestLikeOnComment();
        Like like = createLike(firstId, null, null);
        Post post = createPost(firstId, null, List.of(like));
        Comment comment = createComment(firstId, null, post);
        when(likeRepository.findByCommentIdAndUserId(firstId, firstId)).thenReturn(Optional.empty());
        when(commentRepository.findById(firstId)).thenReturn(Optional.of(comment));

        assertThrows(ConcurrentLikeException.class, () -> likeService.putLikeOnComment(firstId));
    }

    @Test
    public void testPositivePutLikeOnCommentSuccessful() {
        includeCheckUser();
        includeSecondNegativeTestLikeOnComment();
        Post post = createPost(firstId, Collections.emptyList(), Collections.emptyList());
        Comment comment = createComment(firstId, Collections.emptyList(), post);
        Like like = createLike(firstId, null, comment);
        when(likeRepository.findByCommentIdAndUserId(firstId, firstId)).thenReturn(Optional.empty());

        likeService.putLikeOnComment(firstId);

        verify(likeRepository, times(1)).save(like);
    }

    @Test
    public void testNegativeRemoveLikeAtCommentWhenUserNotFound() {
        assertThrows(NullPointerException.class, () -> likeService.removeLikeAtComment(firstId));
    }

    @Test
    public void testNegativeRemoveLikeAtCommentWhenInvalidId() {
        includeCheckUser();

        assertThrows(EntityNotFoundException.class, () -> likeService.removeLikeAtComment(firstId));
    }

    @Test
    public void testNegativeRemoveLikeAtCommentWhenCommentNotFound() {
        includeCheckUser();
        when(likeRepository.findByCommentIdAndUserId(firstId, firstId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> likeService.removeLikeAtComment(firstId));
    }

    @Test
    public void testPositiveRemoveLikeAtCommentSuccessful() {
        includeCheckUser();
        Like like = createLike(firstId, null, createComment(firstId, null, null));
        when(likeRepository.findByCommentIdAndUserId(firstId, firstId)).thenReturn(Optional.ofNullable(like));

        likeService.removeLikeAtComment(firstId);

        verify(likeRepository, times(1)).deleteByCommentIdAndUserId(firstId, firstId);
    }

    private Post createPost(Long id, List<Comment> comments, List<Like> likes) {
        return Post.builder()
                .id(id)
                .likes(likes)
                .comments(comments)
                .build();
    }

    private Comment createComment(Long id, List<Like> likes, Post post) {
        return Comment.builder()
                .id(id)
                .likes(likes)
                .post(post)
                .build();
    }

    private Like createLike(Long id, Post post, Comment comment) {
        return Like.builder()
                .userId(id)
                .comment(comment)
                .post(post)
                .build();
    }

    private UserDto createUser(Long id) {
        return UserDto.builder()
                .id(id)
                .build();
    }

    private void includeSecondNegativeTestLikeOnPost() {
        Optional<Post> post = Optional.ofNullable(
                createPost(firstId, Collections.emptyList(), Collections.emptyList()));
        when(postRepository.findById(firstId)).thenReturn(post);
    }

    private void includeSecondNegativeTestLikeOnComment() {
        Optional<Comment> comment = Optional.ofNullable(
                createComment(firstId, Collections.emptyList(),
                        createPost(firstId, Collections.emptyList(), Collections.emptyList())));
        when(commentRepository.findById(firstId)).thenReturn(comment);
    }

    private void includeCheckUser() {
        when(userContext.getUserId()).thenReturn(firstId);
        when(userClient.getUser(firstId)).thenReturn(createUser(firstId));
    }
}
